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

import com.github.serivesmejia.eocvsim.pipeline.PipelineManager
import com.github.serivesmejia.eocvsim.pipeline.PipelineSource
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.SysUtil
import com.qualcomm.robotcore.util.ElapsedTime
import com.sun.tools.javac.api.JavacTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openftc.easyopencv.OpenCvPipeline
import java.io.File

class CompiledPipelineManager(private val pipelineManager: PipelineManager) {

    companion object {
        val DEF_WORKSPACE_FOLDER  = File(SysUtil.getEOCVSimFolder(), File.separator + "default_workspace").mkdirLazy()

        val COMPILER_FOLDER       = File(SysUtil.getEOCVSimFolder(), File.separator + "compiler").mkdirLazy()

        val SOURCES_OUTPUT_FOLDER = File(COMPILER_FOLDER, File.separator + "gen_src").mkdirLazy()
        val CLASSES_OUTPUT_FOLDER = File(COMPILER_FOLDER, File.separator + "out_classes").mkdirLazy()
        val JARS_OUTPUT_FOLDER    = File(COMPILER_FOLDER, File.separator + "out_jars").mkdirLazy()

        val PIPELINES_OUTPUT_JAR  = File(JARS_OUTPUT_FOLDER, File.separator + "pipelines.jar")

        val IS_USABLE by lazy {
            val usable = try {
                // try getting the JavacTool (only available on JDKs)
                JavacTool.create()
                true // yes! we can compile stuff on runtime!
            } catch(ignored: Throwable) {
                false // uh oh, the JavacTool class wasn't found anywhere in the current JVM...
            }

            // Send a warning message to console
            // will only be sent once (that's why it's done here)
            if(!usable) {
                Log.warn(TAG, "Unable to compile Java source code in this JVM (the class JavacTool wasn't found)")
                Log.warn(TAG, "For the user, this probably means that the sim is running in a JRE which doesn't include the javac compiler executable")
                Log.warn(TAG, "To be able to compile pipelines on runtime, make sure the sim is running on a JDK that includes the javac executable (any JDK probably does)")
            }

            usable
        }

        val TAG = "CompiledPipelineManager"
    }

    var workspace: File
        set(value) { pipelineManager.eocvSim.config.workspacePath = value.absolutePath }
        get() = File(pipelineManager.eocvSim.config.workspacePath)

    var currentPipelineClassLoader: PipelineClassLoader? = null
        private set

    fun init() {
        Log.info(TAG, "Initializing...")

        asyncCompile { result ->
            // if the build failed, try to load the
            // latest pipelines.jar file, if it exists
            if(result.status == PipelineCompileStatus.FAILED)
                loadFromPipelinesJar()
        }
    }

    fun compile(): PipelineCompileResult {
        if(!IS_USABLE) return PipelineCompileResult(
            PipelineCompileStatus.FAILED,
            "Current JVM does not have a javac executable (a JDK is needed)"
        )

        Log.info(TAG, "Building java files in workspace at ${workspace.absolutePath}")

        val runtime = ElapsedTime()

        val compiler = PipelineCompiler(workspace)
        val result = compiler.compile(PIPELINES_OUTPUT_JAR)

        val timeElapsed = runtime.seconds()

        pipelineManager.requestRemoveAllPipelinesFrom(PipelineSource.COMPILED_ON_RUNTIME, false)

        currentPipelineClassLoader = null

        val messageEnd = "(took $timeElapsed seconds)\n${result.message}".trim()

        when(result.status) {
            PipelineCompileStatus.SUCCESS -> {
                Log.info(TAG, "Build successful $messageEnd")
                loadFromPipelinesJar()
            }
            PipelineCompileStatus.NO_SOURCE -> {
                Log.warn(TAG, "Build cancelled, no source files to compile $messageEnd")
                //delete jar if we had no sources, the most logical outcome in this case
                if(PIPELINES_OUTPUT_JAR.exists()) PIPELINES_OUTPUT_JAR.delete()
            }
            else -> {
                Log.warn(TAG, "Build failed $messageEnd")
            }
        }

        return result
    }

    fun asyncCompile(endCallback: (PipelineCompileResult) -> Unit = {}) = GlobalScope.launch(Dispatchers.IO) {
        endCallback(compile())
    }

    fun loadFromPipelinesJar() {
        if(!PIPELINES_OUTPUT_JAR.exists()) return

        Log.info(TAG, "Looking for pipelines in jar file $PIPELINES_OUTPUT_JAR")

        try {
            currentPipelineClassLoader = PipelineClassLoader(PIPELINES_OUTPUT_JAR)

            val pipelines = mutableListOf<Class<out OpenCvPipeline>>()

            for(pipelineClass in currentPipelineClassLoader!!.pipelineClasses) {
                pipelines.add(pipelineClass)
                Log.info(TAG, "Added ${pipelineClass.simpleName} from jar")
            }

            pipelineManager.requestAddPipelineClasses(pipelines, PipelineSource.COMPILED_ON_RUNTIME, true)
        } catch(e: Exception) {
            Log.error(TAG, "Uncaught exception thrown while loading jar $PIPELINES_OUTPUT_JAR", e)
        }
    }

}

private fun File.mkdirLazy() = apply { mkdir() }