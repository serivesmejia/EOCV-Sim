package com.github.serivesmejia.eocvsim.tuner

interface TunableFieldAcceptor {
    fun accept(clazz: Class<*>): Boolean
}