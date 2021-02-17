package com.github.serivesmejia.eocvsim.util.extension

import kotlinx.coroutines.*
import java.lang.ref.WeakReference

object CoroutineExt {

    private var closeRequestedDispatchers = ArrayList<WeakReference<ExecutorCoroutineDispatcher>>()
    val ExecutorCoroutineDispatcher.isCloseRequested: Boolean
        get() {
            for(dispatcherRef in closeRequestedDispatchers.toTypedArray()) {
                val dispatcher = dispatcherRef.get()

                if(dispatcher == this) {
                    return true
                } else if(dispatcher == null) {
                    closeRequestedDispatchers.remove(dispatcherRef)
                }
            }
            return false
        }

    val ExecutorCoroutineDispatcher.hasFinished: Boolean
        get() {
            this[Job]?.let {
                return it.isCompleted && isCloseRequested
            }
            return isCloseRequested && !isActive
        }

    fun ExecutorCoroutineDispatcher.requestClose() {
        closeRequestedDispatchers.add(WeakReference(this))
        close()
    }

}