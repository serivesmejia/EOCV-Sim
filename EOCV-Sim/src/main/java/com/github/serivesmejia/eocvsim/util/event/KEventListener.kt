package com.github.serivesmejia.eocvsim.util.event

class KEventListener(val listener: (Int) -> Unit) : EventListener() {

    override fun run() {
        listener(id)
    }

}