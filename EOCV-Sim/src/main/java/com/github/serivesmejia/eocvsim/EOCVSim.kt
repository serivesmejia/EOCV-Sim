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

package com.github.serivesmejia.eocvsim

import com.github.serivesmejia.eocvsim.config.ConfigManager
import com.github.serivesmejia.eocvsim.gui.DialogFactory
import com.github.serivesmejia.eocvsim.gui.Visualizer
import com.github.serivesmejia.eocvsim.gui.dialog.FileAlreadyExists
import com.github.serivesmejia.eocvsim.input.InputSourceManager
import com.github.serivesmejia.eocvsim.output.VideoRecordingSession
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager
import com.github.serivesmejia.eocvsim.tuner.TunerManager
import com.github.serivesmejia.eocvsim.util.exception.EOCVSimUncaughtExceptionHandler
import com.github.serivesmejia.eocvsim.util.FileFilters

import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import com.github.serivesmejia.eocvsim.util.extension.FileExt.plus
import com.github.serivesmejia.eocvsim.util.fps.FpsCounter
import com.github.serivesmejia.eocvsim.util.fps.FpsLimiter

import nu.pattern.OpenCV
import org.opencv.core.Size
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter

class EOCVSim(val params: Parameters = Parameters()) {

    companion object {
        const val VERSION = "2.2.0"
        const val DEFAULT_EOCV_WIDTH = 320
        const val DEFAULT_EOCV_HEIGHT = 240
        @JvmField val DEFAULT_EOCV_SIZE = Size(DEFAULT_EOCV_WIDTH.toDouble(), DEFAULT_EOCV_HEIGHT.toDouble())
        @Volatile private var alreadyInitializedOnce = false
    }

    @JvmField val onMainUpdate = EventHandler("OnMainUpdate")

    @JvmField val visualizer = Visualizer(this)

    @JvmField val configManager = ConfigManager()
    @JvmField val inputSourceManager = InputSourceManager(this)
    @JvmField val pipelineManager = PipelineManager(this)

    @JvmField var tunerManager = TunerManager(this)

    var currentRecordingSession: VideoRecordingSession? = null

    val fpsLimiter = FpsLimiter(30.0)

    enum class DestroyReason {
        USER_REQUESTED, THEME_CHANGING, RESTART
    }

    fun init() {

        Log.info("EOCVSim", "Initializing EasyOpenCV Simulator v$VERSION")
        Log.white()

        EOCVSimUncaughtExceptionHandler.register()

        //loading native lib only once in the app runtime
        if (!alreadyInitializedOnce) {
            Log.info("EOCVSim", "Loading native lib...")
            try {
                OpenCV.loadLocally()
                Log.info("EOCVSim", "Successfully loaded native lib")
            } catch (ex: Throwable) {
                Log.error("EOCVSim", "Failure loading native lib", ex)
                Log.info("EOCVSim", "Retrying with old method...")
                SysUtil.loadCvNativeLib()
            }
            Log.white()
        }

        alreadyInitializedOnce = true

        configManager.init() //load config

        visualizer.initAsync(configManager.config.simTheme) //create gui in the EDT
        inputSourceManager.init() //loading user created input sources
        pipelineManager.init() //init pipeline manager (scan for pipelines)
        tunerManager.init() //init tunable variables manager

        visualizer.waitForFinishingInit()

        visualizer.updateSourcesList() //update sources and pick first one
        visualizer.sourceSelector.selectedIndex = 0
        visualizer.updatePipelinesList() //update pipelines and pick first one (DefaultPipeline)
        visualizer.pipelineSelector.selectedIndex = 0

        beginLoop()
    }

    private fun beginLoop() {

        Log.info("EOCVSim", "Begin EOCVSim loop")
        Log.white()

        inputSourceManager.inputSourceLoader.saveInputSourcesToFile()

        pipelineManager.pipelineOutputPoster = visualizer.viewport.matPoster

        pipelineManager.pipelineOutputPoster?.addPostable {
            try {
                //if there's an ongoing recording session, post the mat to the recording
                currentRecordingSession?.postMatAsync(it)
            } catch (ex: Exception) {
                Log.error("EOCVSim", "Error while posting Mat to ecording", ex)
            }

            //updating displayed telemetry
            visualizer.updateTelemetry(pipelineManager.currentTelemetry)
        }

        while (!Thread.interrupted()) {
            //run all pending requested runnables
            onMainUpdate.run()

            updateVisualizerTitle()

            inputSourceManager.update(pipelineManager.paused)
            tunerManager.update()

            //if we don't have a mat from the inputsource, we'll just skip this frame.
            if (inputSourceManager.lastMatFromSource == null || inputSourceManager.lastMatFromSource.empty()) continue

            pipelineManager.update(inputSourceManager.lastMatFromSource)

            //limit FPS
            fpsLimiter.maxFPS = configManager.config.maxFps.toDouble()
            try {
                fpsLimiter.sync();
            } catch(ex: InterruptedException) {
                break
            }
        }

        Log.warn("EOCVSim", "Main thread interrupted (" + Integer.toHexString(hashCode()) + ")")

    }

    fun destroy(reason: DestroyReason) {
        val hexCode = Integer.toHexString(this.hashCode())

        Log.warn("EOCVSim", "Destroying current EOCVSim ($hexCode) due to $reason")

        currentRecordingSession?.stopRecordingSession()
        currentRecordingSession?.discardVideo()

        Log.info("EOCVSim", "Trying to save config file...")

        configManager.saveToFile()
        visualizer.close()

        Thread.currentThread().interrupt()
    }

    fun destroy() {
        destroy(DestroyReason.USER_REQUESTED)
    }

    fun restart() {
        Log.info("EOCVSim", "Restarting...")

        Log.white()
        destroy(DestroyReason.RESTART)
        Log.white()

        Thread({ EOCVSim().init() }, "main").start() //run next instance on a separate thread for the old one to get interrupted and ended
    }

    fun startRecordingSession() {
        if(currentRecordingSession == null) {
            currentRecordingSession = VideoRecordingSession(fpsLimiter.maxFPS, configManager.config.videoRecordingSize)
            currentRecordingSession!!.startRecordingSession()
        }
    }

    //stopping recording session and saving file
    fun stopRecordingSession() {
        currentRecordingSession?.let { itVideo ->

            visualizer.pipelineRecordBtt.isEnabled = false

            itVideo.stopRecordingSession()

            DialogFactory.createFileChooser(visualizer.frame, DialogFactory.FileChooser.Mode.SAVE_FILE_SELECT, FileFilters.recordedVideoFilter)
                    .addCloseListener { _: Int, file: File?, selectedFileFilter: FileFilter? ->
                        onMainUpdate.doOnce {
                            if(file != null) {

                                var correctedFile = File(file.absolutePath)
                                val extension = SysUtil.getExtensionByStringHandling(file.name)

                                if (selectedFileFilter is FileNameExtensionFilter) { //if user selected an extension
                                    //get selected extension
                                    correctedFile = file + "." + selectedFileFilter.extensions[0]
                                } else if(extension.isPresent) {
                                    if(!extension.get().equals("avi", true)) {
                                        correctedFile = file + ".avi"
                                    }
                                } else {
                                    correctedFile = file + ".avi"
                                }

                                if (correctedFile.exists()) {
                                    SwingUtilities.invokeLater {
                                        if (DialogFactory.createFileAlreadyExistsDialog(this) == FileAlreadyExists.UserChoice.REPLACE) {
                                            onMainUpdate.doOnce { itVideo.saveTo(correctedFile) }
                                        }
                                    }
                                } else {
                                    itVideo.saveTo(correctedFile)
                                }
                            } else {
                                itVideo.discardVideo()
                            }

                            currentRecordingSession = null
                            visualizer.pipelineRecordBtt.isEnabled = true
                        }
                    }
        }
    }

    fun isCurrentlyRecording() = currentRecordingSession?.isRecording ?: false

    private fun updateVisualizerTitle() {
        val pipelineFpsMsg = " (${pipelineManager.pipelineFpsCounter.fps} Pipeline FPS)"
        val posterFpsMsg = " (${visualizer.viewport.matPoster.fpsCounter.fps} Poster FPS)"
        val isPaused = if (pipelineManager.paused) " (Paused)" else ""
        val isRecording = if (isCurrentlyRecording()) " RECORDING" else ""
        val memoryMsg = " (${SysUtil.getMemoryUsageMB()} MB Java memory used)"

        val msg = isRecording + pipelineFpsMsg + posterFpsMsg + isPaused + memoryMsg

        if (pipelineManager.currentPipeline == null) {
            visualizer.setTitleMessage("No pipeline$msg")
        } else {
            visualizer.setTitleMessage(pipelineManager.currentPipelineName + msg)
        }
    }

    class Parameters {
        var scanForPipelinesIn = "org.firstinspires"
        var scanForTunableFieldsIn = "com.github.serivesmejia"
    }

}