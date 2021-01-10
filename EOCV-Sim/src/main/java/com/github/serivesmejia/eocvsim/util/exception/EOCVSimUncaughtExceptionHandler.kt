package com.github.serivesmejia.eocvsim.util.exception

import com.github.serivesmejia.eocvsim.util.Log
import kotlin.system.exitProcess

class EOCVSimUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    companion object {
        fun register() {
            Thread.setDefaultUncaughtExceptionHandler(EOCVSimUncaughtExceptionHandler())
        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if(e is Exception) {
            Log.error("Uncaught exception thrown in \"${t.name}\" thread, the application will exit now", e);
            CrashReport(e).saveCrashReport()

            Log.warn("If this error persists, open an issue on EOCV-Sim's GitHub page attaching the crash report file.");
            exitProcess(1)
        }
    }

}