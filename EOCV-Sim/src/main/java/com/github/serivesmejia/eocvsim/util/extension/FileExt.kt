package com.github.serivesmejia.eocvsim.util.extension

import java.io.File

object FileExt {
    operator fun File.plus(str: String): File {
        return File(this.absolutePath + str)
    }
}