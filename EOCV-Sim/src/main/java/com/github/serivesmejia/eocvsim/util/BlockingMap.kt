package com.github.serivesmejia.eocvsim.util

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap

class BlockingMap<K, V> {

    private val map = ConcurrentHashMap<K, ArrayBlockingQueue<V>>()

    private fun getQueue(key: K, replace: Boolean): ArrayBlockingQueue<V>? {
        return map.compute(key, { _, v ->
            if(replace || v == null) {
                ArrayBlockingQueue(1)
            } else {
                v
            }
        })
    }

    fun put(key: K, value: V) {
        getQueue(key, true)?.add(value)
    }

    fun get(key: K) = getQueue(key, false)?.take();

}