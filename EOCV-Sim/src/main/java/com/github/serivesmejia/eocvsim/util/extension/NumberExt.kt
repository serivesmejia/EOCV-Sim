package com.github.serivesmejia.eocvsim.util.extension

object NumberExt {

    fun Int.clipUpperZero(): Int {
        return if(this > 0) {
            this
        } else {
            this + 1
        }
    }

}