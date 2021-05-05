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

import com.github.serivesmejia.eocvsim.gui.DialogFactory
import com.github.serivesmejia.eocvsim.gui.dialog.BuildOutput
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager
import com.github.serivesmejia.eocvsim.pipeline.PipelineSource
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import com.qualcomm.robotcore.util.ElapsedTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        const val TAG = "CompiledPipelineManager"
    }

    var currentPipelineClassLoader: PipelineClassLoader? = null
        private set

    val onBuildStart = EventHandler("CompiledPipelineManager-OnBuildStart")
    val onBuildEnd   = EventHandler("CompiledPipelineManager-OnBuildEnd")

    var lastBuildResult: PipelineCompileResult? = null
        private set
    var lastBuildOutputMessage: String? = null
        private set

    var isBuildRunning = false
        private set

    val workspaceManager get() = pipelineManager.eocvSim.workspaceManager

    fun init() {
        Log.info(TAG, "Initializing...")
        asyncCompile()
    }

    fun compile(): PipelineCompileResult {
        isBuildRunning = true
        onBuildStart.run()

        if(!PipelineCompiler.IS_USABLE) {
            lastBuildResult = PipelineCompileResult(
                PipelineCompileStatus.FAILED,
                "Current JVM does not have a javac executable (a JDK is needed)"
            )
            lastBuildOutputMessage = null

            onBuildEnd.run()
            isBuildRunning = false

            return lastBuildResult!!
        }

        workspaceManager.reloadConfig()

        val absoluteSourcesPath = workspaceManager.sourcesAbsolutePath.toFile()
        
        Log.info(TAG, "Building java files in workspace, at ${absoluteSourcesPath.absolutePath}")

        val runtime = ElapsedTime()

        val compiler = PipelineCompiler(workspaceManager.sourceFiles, absoluteSourcesPath)
        
        val result = compiler.compile(PIPELINES_OUTPUT_JAR)
        lastBuildResult = result

        val timeElapsed = String.format("%.2f", runtime.seconds())

        pipelineManager.requestRemoveAllPipelinesFrom(PipelineSource.COMPILED_ON_RUNTIME, false)

        currentPipelineClassLoader = null

        val messageEnd = "(took $timeElapsed seconds)\n\n${result.message}".trim()

        lastBuildOutputMessage = when(result.status) {
            PipelineCompileStatus.SUCCESS -> {
                loadFromPipelinesJar()
                "Build successful $messageEnd"
            }
            PipelineCompileStatus.NO_SOURCE -> {
                deleteJarFile()
                if(pipelineManager.eocvSim.visualizer.hasFinishedInit())
                    pipelineManager.refreshGuiPipelineList()

                "Build cancelled, no source files to compile $messageEnd"
            }
            else -> {
                deleteJarFile()
                "Build failed $messageEnd"
            }
        }

        if(result.status == PipelineCompileStatus.SUCCESS) {
            Log.info(TAG, "$lastBuildOutputMessage\n")
        } else {
            Log.warn(TAG, "$lastBuildOutputMessage\n")

            if(result.status == PipelineCompileStatus.FAILED && !BuildOutput.isAlreadyOpened)
                DialogFactory.createBuildOutput(pipelineManager.eocvSim)
        }

        onBuildEnd.run()
        isBuildRunning = false

        return result
    }

    fun asyncCompile(endCallback: (PipelineCompileResult) -> Unit = {}) = GlobalScope.launch(Dispatchers.IO) {
        endCallback(compile())
    }

    private fun deleteJarFile() {
        //delete jar if we had no sources, the most logical outcome in this case
        if(PIPELINES_OUTPUT_JAR.exists()) PIPELINES_OUTPUT_JAR.delete()
        currentPipelineClassLoader = null
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