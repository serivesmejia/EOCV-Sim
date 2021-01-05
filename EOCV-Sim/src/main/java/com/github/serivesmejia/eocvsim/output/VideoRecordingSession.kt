package com.github.serivesmejia.eocvsim.output

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.videoio.VideoWriter

class VideoRecordingSession(val filepath: String, val fps: Double = 30.0, val size: Size = Size(320.0, 240.0)) {

    val videoWriter = VideoWriter()

    fun startRecordingSession() {
        videoWriter.open(filepath, VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, size)
    }

    fun stopRecordingSession() {
        videoWriter.release()
    }

    fun postMat(m: Mat) {
        if(!videoWriter.isOpened) return
        videoWriter.write(m);
    }

}