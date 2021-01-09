package com.github.serivesmejia.eocvsim.output

import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.extension.CvExt.aspectRatio
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoWriter
import org.openftc.easyopencv.MatRecycler
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class VideoRecordingSession(val fps: Double = 30.0, val size: Size = Size(320.0, 240.0), val isFramesRgb: Boolean = true) {

    val videoWriter = VideoWriter()
    val tempFile = File.createTempFile(Math.random().toString(), ".avi")

    var hasStarted = false
        private set
    var hasStopped = false
        private set

    private val matRecycler = MatRecycler(2)

    fun startRecordingSession() {
        videoWriter.open(tempFile.toString(), VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, size)
        hasStarted = true;
    }

    fun stopRecordingSession() {
        videoWriter.release()
        matRecycler.releaseAll()
        hasStopped = true
    }

    fun saveTo(file: File) {
        if(!hasStopped) return
        Files.copy(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        Files.delete(tempFile.toPath())
    }

    fun postMat(inputMat: Mat) {

        val mat = matRecycler.takeMat();

        if(isFramesRgb) {
            Imgproc.cvtColor(inputMat, mat, Imgproc.COLOR_RGB2BGR)
        } else {
            inputMat.copyTo(mat)
        }

        if(inputMat.size() == size) { //nice, the mat size is the exact same as the video size
            videoWriter.write(mat);
        } else { //uh oh, this might get a bit harder here...

            val videoR = size.aspectRatio()
            val inputR = inputMat.aspectRatio()

            val inputW = inputMat.size().width
            val inputH = inputMat.size().height

            val videoW = size.width
            val videoH = size.height

            //ok, we have the same aspect ratio, we can just scale to the required size
            if(videoR == inputR) {
                Imgproc.resize(mat, mat, size, 0.0, 0.0, Imgproc.INTER_AREA)
                videoWriter.write(mat)
                Log.info("b")
            } else { //hmm, not the same aspect ratio, we'll need to do some fancy stuff here...

                val newSize = if(videoR > inputR) {
                    Size(inputW * videoH / inputH, videoH)
                } else {
                    Size(videoW, inputH * videoW / inputW)
                }

                Imgproc.resize(mat, mat, newSize, 0.0, 0.0, Imgproc.INTER_AREA)
                videoWriter.write(mat)
                Log.info("c")

            }

            mat.returnMat();

        }

    }

}