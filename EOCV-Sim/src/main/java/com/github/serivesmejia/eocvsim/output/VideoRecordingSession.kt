package com.github.serivesmejia.eocvsim.output

import com.github.serivesmejia.eocvsim.gui.util.MatPoster
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.StrUtil
import com.github.serivesmejia.eocvsim.util.extension.CvExt.aspectRatio
import com.github.serivesmejia.eocvsim.util.extension.CvExt.clipTo
import com.github.serivesmejia.eocvsim.util.fps.FpsCounter
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.math.roundToInt

class VideoRecordingSession(val fps: Double = 30.0, val videoSize: Size = Size(320.0, 240.0), val isFramesRgb: Boolean = true) {

    private val videoWriter = VideoWriter()
    private val tempFile = File.createTempFile(StrUtil.random(), ".avi")

    @Volatile private var videoMat: Mat? = null

    private val matPoster = MatPoster(fps.toInt())

    private val fpsCounter = FpsCounter()

    @Volatile var hasStarted = false
        private set
    @Volatile var hasStopped = false
        private set

    val isRecording: Boolean
        get() {
            return hasStarted && !hasStopped
        }

    init {
        matPoster.addPostable { postMat(it) }
    }

    fun startRecordingSession() {
        videoWriter.open(tempFile.toString(), VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, videoSize)
        hasStarted = true;
    }

    fun stopRecordingSession() {
        videoWriter.release(); videoMat?.release(); matPoster.stop()
        hasStopped = true
    }

    fun saveTo(file: File) {
        if(!hasStopped) return
        Files.copy(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        Files.delete(tempFile.toPath())
    }

    @Synchronized fun postMatAsync(inputMat: Mat) {
        if(!videoWriter.isOpened) return
        matPoster.post(inputMat)
    }

    @Synchronized fun postMat(inputMat: Mat) {

        if(!videoWriter.isOpened) return

        if(videoMat == null)
            videoMat = Mat(videoSize, inputMat.type())
        else
            videoMat!!.setTo(Scalar(0.0, 0.0, 0.0))

        //we need BGR frames
        if(isFramesRgb) {
            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_RGB2BGR)
        }

        if(inputMat.size() == videoSize) { //nice, the mat size is the exact same as the video size
            compensateFpsWrite(inputMat, fpsCounter.fps.toDouble(), fps)
        } else { //uh oh, this might get a bit harder here...

            val videoR = videoSize.aspectRatio()
            val inputR = inputMat.aspectRatio()

            //ok, we have the same aspect ratio, we can just scale to the required size
            if(videoR == inputR) {
                Imgproc.resize(inputMat, videoMat, videoSize, 0.0, 0.0, Imgproc.INTER_AREA)
                compensateFpsWrite(videoMat!!, fpsCounter.fps.toDouble(), fps)
            } else { //hmm, not the same aspect ratio, we'll need to do some fancy stuff here...

                val inputW = inputMat.size().width
                val inputH = inputMat.size().height

                val widthRatio = videoSize.width / inputW
                val heightRatio = videoSize.height / inputH
                val bestRatio = widthRatio.coerceAtMost(heightRatio)

                val newSize = Size(inputW * bestRatio, inputH * bestRatio).clipTo(videoSize)

                //get offsets so that we center the image instead of leaving it at (0,0)
                //(basically the black bars you see)
                val xOffset = (videoSize.width - newSize.width) / 2
                val yOffset = (videoSize.height - newSize.height) / 2

                Imgproc.resize(inputMat, inputMat, newSize, 0.0, 0.0, Imgproc.INTER_AREA)

                val submat = videoMat!!.submat(Rect(Point(xOffset, yOffset), newSize))
                inputMat.copyTo(submat);

                compensateFpsWrite(videoMat!!, fpsCounter.fps.toDouble(), fps)

            }

            fpsCounter.update()

        }
    }

    @Synchronized private fun compensateFpsWrite(mat: Mat, currentFps: Double, targetFps: Double) {
        if (currentFps < targetFps && currentFps > 0) {
            val rep = (targetFps / currentFps).roundToInt()
            Log.info("$rep $currentFps, $targetFps")
            repeat(rep) {
                videoWriter.write(mat)
            }
        } else {
            videoWriter.write(mat)
        }
    }

}