package com.github.serivesmejia.eocvsim.util.fps

class FpsLimiter(var maxFPS: Int = 30) {

    var start = 0.0
    var diff = 0.0
    var wait = 0.0

    @Throws(InterruptedException::class)
    fun sync() {
        wait = 1.0 / (maxFPS.toDouble() / 1000.0)
        diff = System.currentTimeMillis() - start
        if (diff < wait) {
            Thread.sleep((wait - diff).toLong())
        }
        start = System.currentTimeMillis().toDouble()
    }

}