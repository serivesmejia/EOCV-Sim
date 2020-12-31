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

    fun doOnce(listener: EventListener): Int {
        idCount++

        internalListeners[idCount] = listener
        listener.id = idCount

        return listener.id
    }

    fun doOnce(runnable: Runnable) = doOnce(KEventListener { runnable.run() })
    fun doOnce(listener: (Int) -> Unit) = doOnce(KEventListener(listener))

    fun doPersistent(listener: EventListener) {
        doOnce(listener)
        listener.persistent = true
    }

    fun doPersistent(runnable: Runnable) = doPersistent(KEventListener { runnable.run() })
    fun doPersistent(listener: (Int) -> Unit) = doPersistent(KEventListener(listener))

    fun getListener(id: Int): EventListener? = internalListeners[id]

    fun getKListener(id: Int): KEventListener? {
        val listener = getListener(id) ?: return null
        return if(listener is KEventListener) { listener } else { null }
    }

    fun removeListener(id: Int) = internalListeners.remove(id)

}