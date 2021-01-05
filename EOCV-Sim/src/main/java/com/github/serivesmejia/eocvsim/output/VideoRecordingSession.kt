package com.github.serivesmejia.eocvsim.output

import org.opencv.core.Size
import org.opencv.videoio.VideoWriter

class VideoRecorder(val filepath: String, val fps: Int = 30, val size: Size = Size(320.0, 240.0)) {

    val videoWriter = VideoWriter()

    fun startRecordingSession() {
        videoWriter.open(filepath, VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, size)
    }

}