package com.github.serivesmejia.eocvsim.util.io

import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.Log
import java.io.File
import java.io.RandomAccessFile
import java.io.IOException
import java.nio.channels.FileLock

class LockFile(pathname: String) : File(pathname) {

    private val raf by lazy { RandomAccessFile(this, "rw") }

    var lock: FileLock? = null
        private set

    val isLocked get() = try {
        raf
        if(lock != null) !tryLock(false) else false
    } catch(ex: Exception) {
        Log.warn(TAG, "Can't open lock file $absolutePath")
        true
    }

    companion object {
        const val TAG = "LockFile"
    }

    init {
        if(isDirectory)
            throw IllegalArgumentException("Lock file cannot be a directory")

        if(!exists())
            SysUtil.saveFileStr(this, "")
    }

    fun tryLock(log: Boolean = true): Boolean {
        return try {
            lock = raf.channel.tryLock()
            if(log) Log.info(TAG, "Probably locked file $absolutePath")
            true
        } catch(ex: Exception) {
            if(log) Log.warn(TAG, "Couldn't lock file $absolutePath", ex);
            false
        }
    }

    fun unlock() {
        lock?.release()
        raf.close()

        lock = null
    }

}

val File.directoryLockFile get() = LockFile(absolutePath + File.separator + ".lock")

val File.isDirectoryLocked: Boolean get() {
    val lock = directoryLockFile
    val isLocked = lock.isLocked

    lock.unlock()
    return isLocked
}

fun File.lockDirectory(): LockFile? {
    if(!isDirectory)
        return null

    val lockFile = directoryLockFile

    if(isDirectoryLocked || !lockFile.tryLock())
        return null

    return lockFile
}
