package com.github.serivesmejia.eocvsim.util.extension

import com.qualcomm.robotcore.util.Range
import org.opencv.core.Mat
import org.opencv.core.Size

object CvExt {

    fun Size.aspectRatio() = width / height
    fun Mat.aspectRatio() = size().aspectRatio()

    fun Size.clipTo(size: Size): Size {
        width = Range.clip(width, 0.0, size.width)
        height = Range.clip(height, 0.0, size.height)
        return this
    }

}