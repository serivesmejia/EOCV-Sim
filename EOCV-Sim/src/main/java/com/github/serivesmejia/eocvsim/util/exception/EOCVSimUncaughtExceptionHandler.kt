package com.github.serivesmejia.eocvsim.util.exception

import com.github.serivesmejia.eocvsim.util.Log
import kotlin.system.exitProcess

class EOCVSimUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    companion object {
        val MAX_UNCAUGHT_EXCEPTIONS_BEFORE_CRASH = 3

        @JvmStatic fun register() {
            Thread.setDefaultUncaughtExceptionHandler(EOCVSimUncaughtExceptionHandler())
        }

        private var uncaughtExceptionsCount = 0
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        uncaughtExceptionsCount++

        Log.error("Uncaught exception thrown in \"${t.name}\" thread", e)
        Log.white()

        CrashReport(e).saveCrashReport()

        Log.warn("If this error persists, open an issue on EOCV-Sim's GitHub page attaching the crash report file.")
        Log.white()

        //Exit if uncaught exception happened in the main thread
        //since we would be basically in a deadlock state if that happened
        if(t.name.equals("main", true) || uncaughtExceptionsCount > MAX_UNCAUGHT_EXCEPTIONS_BEFORE_CRASH) {
            Log.warn("The application will exit now (exit code 1)")
            exitProcess(1)
        } else {
            //if not, eocv sim might still be working (i.e a crash from a MatPoster thread)
            //so we might not need to exit in this point, but we'll need to send a warnin
            //to the user
            Log.warn("The application might not work as expected from this point")
        }
    }

}