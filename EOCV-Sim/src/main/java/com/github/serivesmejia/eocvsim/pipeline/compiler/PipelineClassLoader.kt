/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.serivesmejia.eocvsim.pipeline.compiler

import com.github.serivesmejia.eocvsim.util.SysUtil
import org.openftc.easyopencv.OpenCvPipeline
import java.io.*
import java.util.zip.ZipFile

class PipelineClassLoader(private val pipelinesJar: File = CompiledPipelineManager.PIPELINES_OUTPUT_JAR) : ClassLoader() {

    private val zipFile = ZipFile(pipelinesJar)

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        var clazz = findLoadedClass(name)

        if(clazz == null) {
            try {
                getResourceAsStream(
                    name.replace('.', '\\') + ".class"
                ).use { inStream ->
                    ByteArrayOutputStream().use { outStream ->
                        SysUtil.copyStream(inStream, outStream)
                        val bytes = outStream.toByteArray()

                        clazz = defineClass(name, bytes, 0, bytes.size)
                        if (resolve) resolveClass(clazz)
                    }
                }
            } catch(e: Exception) {
                e.printStackTrace()
                clazz = super.loadClass(name, resolve)
            }
        }

        return clazz
    }

    override fun getResourceAsStream(name: String): InputStream? {
        val entry = zipFile.getEntry(name)

        if(entry != null) {
            println("found entry")
            try {
                return zipFile.getInputStream(entry)
            } catch (e: IOException) { }
        }

        return null
    }

}

val OpenCvPipeline.isFromRuntimeCompilation
   get() = this::class.java.classLoader is PipelineClassLoader