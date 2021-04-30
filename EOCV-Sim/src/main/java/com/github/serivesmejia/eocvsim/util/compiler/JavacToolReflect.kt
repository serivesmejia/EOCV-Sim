package com.github.serivesmejia.eocvsim.util.compiler

import com.sun.source.util.JavacTask
import java.io.Writer
import java.nio.charset.Charset
import java.util.*
import javax.tools.DiagnosticListener
import javax.tools.JavaFileManager
import javax.tools.JavaFileObject
import javax.tools.StandardJavaFileManager

class JavacToolReflect {

    companion object {
        private val javacToolClass = Class.forName("com.sun.tools.javac.api.JavacTool")

        private val createMethod = javacToolClass.getDeclaredMethod("create")

        private val getTaskMethod = javacToolClass.getDeclaredMethod(
            "getTask",
            Writer::class.java,
            JavaFileManager::class.java,
            DiagnosticListener::class.java,
            Iterable::class.java,
            Iterable::class.java,
            Iterable::class.java
        )

        private val getStandardFileManagerMethod = javacToolClass.getDeclaredMethod(
            "getStandardFileManager",
            DiagnosticListener::class.java,
            Locale::class.java,
            Charset::class.java
        )
    }

    val javacTool = createMethod.invoke(null)

    fun getTask(
        out: Writer?,
        fileManager: JavaFileManager,
        diagnosticListener: DiagnosticListener<out JavaFileObject>?,
        options: Iterable<String>?, classes: Iterable<String>?,
        compilationUnits: Iterable<JavaFileObject>?
    ) = getTaskMethod.invoke(
        javacTool, out, fileManager, diagnosticListener, options, classes, compilationUnits
    ) as JavacTask

    fun getStandardFileManager(
        diagnosticListener: DiagnosticListener<out JavaFileObject>?,
        locale: Locale?,
        charset: Charset?
    ) = getStandardFileManagerMethod.invoke(
        javacTool, diagnosticListener, locale, charset
    ) as StandardJavaFileManager

}