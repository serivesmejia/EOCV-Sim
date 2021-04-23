package com.github.serivesmejia.eocvsim.util.extension

object StrExt {

    fun String.removeFromEnd(rem: String): String {
        if(endsWith(rem)) {
            return substring(0, length - rem.length).trim()
        }
        return trim()
    }

}