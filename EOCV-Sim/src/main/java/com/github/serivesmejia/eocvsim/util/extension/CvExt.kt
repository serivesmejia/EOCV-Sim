package com.github.serivesmejia.eocvsim.util.extension

import org.opencv.core.Mat
import org.opencv.core.Size

object CvExt {

    fun Size.aspectRatio() = this.height / this.width
    fun Mat.aspectRatio() = this.size().aspectRatio()

}