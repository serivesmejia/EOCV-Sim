package com.github.serivesmejia.eocvsim.util.fps

import com.qualcomm.robotcore.util.ElapsedTime

class FpsCounter {

    private val elapsedTime = ElapsedTime()

    @Volatile
    private var fpsC = 0

    @get:Synchronized
    @Volatile
    var fps = 0
        private set

    @Synchronized
    fun update() {
        fpsC++
        if (elapsedTime.seconds() >= 1) {
            fps = fpsC
            fpsC = 0
            elapsedTime.reset()
        }
    }

}