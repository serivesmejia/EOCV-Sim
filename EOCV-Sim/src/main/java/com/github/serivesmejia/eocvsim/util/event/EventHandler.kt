package com.github.serivesmejia.eocvsim.util.event

import com.github.serivesmejia.eocvsim.util.Log

class EventHandler(val name: String) : Runnable {

    val listeners
        get() = internalListeners.values.toTypedArray()

    private val internalListeners: HashMap<Int, EventListener> = HashMap()

    companion object {
        private var idCount = 0;
    }

    override fun run() {
        for(listener in listeners) {

            try {
                listener.run()
            } catch (ex: Exception) {
                Log.error("${name}-EventHandler", "Error while running listener #${listener.id} (${listener.javaClass})", ex);
            }

            if(!listener.persistent) {
                removeListener(listener.id)
            }

        }
    }

    fun addListener(listener: EventListener): Int {

        idCount++

        internalListeners[idCount] = listener
        listener.id = idCount

        return listener.id

    }

    fun addListener(listener: (Int) -> Unit) = addListener(KEventListener { listener(it) })

    fun addListener(runnable: Runnable) = addListener(KEventListener { runnable.run() })

    fun addPersistentListener(listener: EventListener) {
        addListener(listener)
        listener.persistent = true
    }

    fun addPersistentListener(listener: (Int) -> Unit) = addPersistentListener(KEventListener { listener(it) })

    fun addPersistentListener(runnable: Runnable) = addPersistentListener(KEventListener { runnable.run() })

    fun removeListener(id: Int) = internalListeners.remove(id)

}