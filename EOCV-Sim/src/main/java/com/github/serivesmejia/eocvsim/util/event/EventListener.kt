package com.github.serivesmejia.eocvsim.util.event

abstract class EventListener : Runnable {

    var id = 0
        internal set

    var persistent = false
        internal set

    abstract override fun run()

}