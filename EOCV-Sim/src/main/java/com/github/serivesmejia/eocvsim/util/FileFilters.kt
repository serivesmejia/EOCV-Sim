package com.github.serivesmejia.eocvsim.util

import javax.swing.filechooser.FileNameExtensionFilter

object FileFilters {

    @JvmField val imagesFilter = FileNameExtensionFilter("Images",
            "jpg", "jpeg", "jpe", "jp2", "bmp", "png", "tiff", "tif")


    @JvmField var videoMediaFilter = FileNameExtensionFilter("Video Media",
            "avi", "mkv", "mov", "mp4")

    @JvmField var recordedVideoFilter = FileNameExtensionFilter("AVI (*.avi)", "avi")

}