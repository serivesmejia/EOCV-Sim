package com.github.serivesmejia.eocvsim.util.compiler

import com.github.serivesmejia.eocvsim.util.SysUtil
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipEntry

object JarPacker {

    fun packClassesUnder(outputJar: File, inputClasses: File, manifest: Manifest = Manifest()) {
        FileOutputStream(outputJar).use { outStream ->
            JarOutputStream(outStream, manifest).use { jarOutStream ->
                for (classFile in SysUtil.filesUnder(inputClasses, ".class")) {
                    val ze = ZipEntry(SysUtil.getRelativePath(inputClasses, classFile).path)
                    ze.time = classFile.lastModified()

                    jarOutStream.putNextEntry(ze)
                    SysUtil.copyStream(classFile, jarOutStream)
                    jarOutStream.closeEntry()
                }
            }
        }
    }

}