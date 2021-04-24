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

@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.github.serivesmejia.eocvsim.pipeline.compiler

import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.compiler.JarPacker
import com.sun.tools.javac.api.JavacTool
import java.io.File
import java.io.PrintWriter
import java.util.*
import javax.tools.*

class PipelineCompiler(private val inputPath: File): DiagnosticListener<JavaFileObject> {

    private var diagnosticBuilder = StringBuilder()

    fun compile(outputJar: File): PipelineCompileResult {
        val files = SysUtil.filesUnder(inputPath, ".java")

        val javac = JavacTool.create()
        val args = arrayListOf(
            "-source", "1.8",
            "-target", "1.8",
            "-g",
            "-encoding", "UTF-8",
            "-Xlint:unchecked",
            "-Xlint:deprecation",
            "-XDuseUnsharedTable=true"
        )
        
        val fileManager = PipelineStandardFileManager(javac.getStandardFileManager(this, null, null))
        fileManager.sourcePath = Collections.singleton(inputPath)

        val javaFileObjects = fileManager.getJavaFileObjects(*files.toTypedArray())

        if(javaFileObjects.iterator().hasNext()) {
            val task = javac.getTask(
                PrintWriter(System.out),
                fileManager,
                this,
                args,
                null,
                javaFileObjects
            )

            val taskSuccess = task.call()
            val message = diagnosticBuilder.toString()

            if(taskSuccess) {
                JarPacker.packClassesUnder(outputJar, fileManager.getLocation(StandardLocation.CLASS_OUTPUT).iterator().next())
                return PipelineCompileResult(PipelineCompileStatus.SUCCESS, message)
            }

            return PipelineCompileResult(PipelineCompileStatus.FAILED, message)
        } else {
            return PipelineCompileResult(PipelineCompileStatus.NO_SOURCE, "No source files")
        }
    }

    override fun report(diagnostic: Diagnostic<out JavaFileObject>) {
        val locale = Locale.getDefault()
        val relativeFile = SysUtil.getRelativePath(inputPath, File(diagnostic.source.name))

        diagnosticBuilder.appendLine(String.format(locale, "%s(%d:%d): %s: %s",
            relativeFile.path,
            diagnostic.lineNumber,
            diagnostic.columnNumber,
            diagnostic.kind,
            diagnostic.getMessage(locale)
        ))
    }

}

enum class PipelineCompileStatus {
    SUCCESS,
    FAILED,
    NO_SOURCE
}

data class PipelineCompileResult(val status: PipelineCompileStatus, val message: String)