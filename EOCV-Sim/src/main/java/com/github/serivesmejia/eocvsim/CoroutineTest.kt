package com.github.serivesmejia.eocvsim

import kotlinx.coroutines.*

fun main() {
    println("program execution begins")
    foo()
    println("foo execution finished")
    while(true);
}

fun foo() {
    val job = GlobalScope.launch {
        Thread.sleep(10000);
        println("job ends (10 seconds passed)")
    }

    runBlocking {
        try {
            withTimeout(5000) {
                println("start awaiting with 5 secs timeout")
                job.join()
            }
        } catch (ex: TimeoutCancellationException) {
            println("out of time")
            job.cancel()
        }
    }
}
