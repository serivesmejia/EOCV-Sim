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

package com.github.serivesmejia.eocvsim.pipeline

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.gui.util.MatPoster
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import com.github.serivesmejia.eocvsim.util.exception.MaxActiveContextsException
import com.github.serivesmejia.eocvsim.util.fps.FpsCounter
import kotlinx.coroutines.*
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Mat
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.TimestampedPipelineHandler
import java.awt.Dimension
import java.lang.reflect.Constructor
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

class PipelineManager(var eocvSim: EOCVSim) {

    companion object {
        const val PIPELINE_TIMEOUT_MS = 4100L
        const val MAX_ALLOWED_ACTIVE_PIPELINE_CONTEXTS = 4
    }

    @JvmField val onUpdate          = EventHandler("OnPipelineUpdate")
    @JvmField val onPipelineChange  = EventHandler("OnPipelineChange")
    @JvmField val onPipelineTimeout = EventHandler("OnPipelineTimeout")
    @JvmField val onPause           = EventHandler("OnPipelinePause")
    @JvmField val onResume          = EventHandler("OnPipelineResume")

    var pipelineOutputPosters: ArrayList<MatPoster> = ArrayList()
    val pipelineFpsCounter = FpsCounter()

    private var hasInitCurrentPipeline = false
    var lastPipelineAction = "processFrame"
        private set

    val pipelines = ArrayList<Class<out OpenCvPipeline>>()

    @Volatile
    var currentPipeline: OpenCvPipeline? = null
        private set
    var currentPipelineName = ""
        private set
    var currentPipelineIndex = -1
        private set

    val activePipelineContexts = ArrayList<ExecutorCoroutineDispatcher>()
    private var currentPipelineContext: ExecutorCoroutineDispatcher? = null

    @Volatile var currentTelemetry: Telemetry? = null
        private set

    @Volatile var paused = false
        private set
        get() {
            if (!field) pauseReason = PauseReason.NOT_PAUSED
            return field
        }

    var pauseReason = PauseReason.NOT_PAUSED
        private set
        get() {
            if (!paused) field = PauseReason.NOT_PAUSED
            return field
        }

    //this will be handling the special pipeline "timestamped" type
    val timestampedPipelineHandler = TimestampedPipelineHandler()
    
    enum class PauseReason {
        USER_REQUESTED, IMAGE_ONE_ANALYSIS, NOT_PAUSED
    }

    fun init() {
        Log.info("PipelineManager", "Initializing...")

        //add default pipeline
        addPipelineClass(DefaultPipeline::class.java)

        //scan for pipelines
        PipelineScanner(eocvSim.params.scanForPipelinesIn).lookForPipelines {
            addPipelineClass(it)
        }

        Log.info("PipelineManager", "Found " + pipelines.size + " pipeline(s)")
        Log.blank()

        requestChangePipeline(0) //change to the default pipeline
    }

    fun update(inputMat: Mat) {
        onUpdate.run()

        if(activePipelineContexts.size > MAX_ALLOWED_ACTIVE_PIPELINE_CONTEXTS) {
            throw MaxActiveContextsException("Current amount of active pipeline coroutine contexts (${activePipelineContexts.size}) is more than the maximum allowed. This generally means that there are multiple pipelines stuck in processFrame() running in the background, check for any lengthy operations in your pipelines.")
        }

        if(paused) return

        timestampedPipelineHandler.update(currentPipeline)

        lastPipelineAction = if(!hasInitCurrentPipeline) {
            "init/processFrame"
        } else {
            "processFrame"
        }

        //run our pipeline in the background until it finishes or gets cancelled
        val pipelineJob = GlobalScope.launch(currentPipelineContext ?: EmptyCoroutineContext) {
            try {
                //if we have a pipeline, we run it right here, passing the input mat
                //given to us. we'll post the frame the pipeline returns as long
                //as we haven't ran out of time (the main loop will not wait it
                //forever to finish its job). if we run out of time, and if the
                //pipeline ever returns, we will not post the frame, since we
                //don't know when it was actually requested, we might even be in
                //a different pipeline at this point. we also call init if we
                //haven't done so.

                if(!hasInitCurrentPipeline) {
                    currentPipeline?.init(inputMat)

                    Log.info("PipelineManager", "Initialized pipeline $currentPipelineName")
                    Log.blank()

                    hasInitCurrentPipeline = true
                }

                //check if we're still active (not timeouted)
                //after initialization
                currentPipeline?.processFrame(inputMat)?.let { outputMat ->
                    if (isActive) {
                        pipelineFpsCounter.update()

                        for (poster in pipelineOutputPosters.toTypedArray()) {
                            try {
                                poster.post(outputMat)
                            } catch (ex: Exception) {
                                Log.error(
                                    "PipelineManager",
                                    "Uncaught exception thrown while posting pipeline output Mat to ${poster.name} poster",
                                    ex
                                )
                            }
                        }
                    } else {
                        activePipelineContexts.remove(this.coroutineContext)
                    }
                }

                //clear error messages in telemetry
                currentTelemetry?.errItem?.caption = ""
                currentTelemetry?.errItem?.setValue("")
            } catch (ex: Exception) { //handling exceptions from pipelines
                currentTelemetry?.errItem?.caption = "[/!\\]"
                currentTelemetry?.errItem?.setValue("Uncaught exception thrown in pipeline\nCheck console for details.")

                Log.error("PipelineManager", "Uncaught exception thrown while processing pipeline $currentPipelineName", ex)
            }
        }

        runBlocking {
            try {
                //allow double timeout if we haven't initialized the pipeline
                val timeout = if(hasInitCurrentPipeline) {
                    PIPELINE_TIMEOUT_MS
                } else {
                    PIPELINE_TIMEOUT_MS * 2
                }

                //ok! this is the part in which we'll wait for the pipeline with a timeout
                withTimeout(timeout) {
                    pipelineJob.join()
                }

                activePipelineContexts.remove(currentPipelineContext)
            } catch (ex: TimeoutCancellationException) {
                //oops, pipeline ran out of time! we'll fall back
                //to default pipeline to avoid further issues.
                requestForceChangePipeline(0)
                //also call the event listeners in case
                //someone wants to do something here
                onPipelineTimeout.run()

                Log.warn("PipelineManager" , "User pipeline $currentPipelineName took too long to $lastPipelineAction (more than $PIPELINE_TIMEOUT_MS ms), falling back to DefaultPipeline.")
                Log.blank()
            } finally {
                //we cancel our pipeline job so that it
                //doesn't post the output mat from the
                //pipeline if it ever returns.
                pipelineJob.cancel()
            }
        }
    }

    fun callViewportTapped() = currentPipeline?.let { pipeline -> //run only if our pipeline is not null
        //similar to pipeline processFrame, call the user function in the background
        //and wait for some X timeout for the user to finisih doing what it has to do.
        val viewportTappedJob = GlobalScope.launch(currentPipelineContext ?: EmptyCoroutineContext) {
            pipeline.onViewportTapped()
        }

        try {
            //perform the timeout here (we'll block for a bit
            //and if it runs out of time, give up and move on)
            runBlocking {
                withTimeout(PIPELINE_TIMEOUT_MS) {
                    viewportTappedJob.join()
                }
            }
        } catch(ex: TimeoutCancellationException) {
            //send a warning to the user
            Log.warn("PipelineManager" , "User pipeline $currentPipelineName took too long to handle onViewportTapped (more than $PIPELINE_TIMEOUT_MS ms).")
        } finally {
            //cancel the job
            viewportTappedJob.cancel()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun addPipelineClass(C: Class<*>) {
        try {
            pipelines.add(C as Class<out OpenCvPipeline>)
        } catch (ex: Exception) {
            Log.error("PipelineManager", "Error while adding pipeline class", ex)
            Log.error("PipelineManager", "Unable to cast " + C.name + " to OpenCvPipeline class.")
            Log.error("PipelineManager", "Remember that the pipeline class should extend OpenCvPipeline")
        }
    }

    /**
     * Changes to the requested pipeline, no matter
     * if we're currently on the same pipeline or not
     */
    @OptIn(ObsoleteCoroutinesApi::class)
    fun forceChangePipeline(index: Int) {
        var nextPipeline: OpenCvPipeline? = null
        var nextTelemetry: Telemetry? = null
        val pipelineClass = pipelines[index]

        Log.info("PipelineManager", "Changing to pipeline " + pipelineClass.name)

        var constructor: Constructor<*>

        try {
            nextTelemetry = Telemetry()

            try { //instantiate pipeline if it has a constructor with a telemetry parameter
                constructor = pipelineClass.getConstructor(Telemetry::class.java)
                nextPipeline = constructor.newInstance(nextTelemetry) as OpenCvPipeline
            } catch (ex: NoSuchMethodException) { //instantiating with a constructor with no params
                constructor = pipelineClass.getConstructor()
                nextPipeline = constructor.newInstance() as OpenCvPipeline
            }

            Log.info("PipelineManager", "Instantiated pipeline class " + pipelineClass.name)
        } catch (ex: NoSuchMethodException) {
            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Check console for details",
                    "Close", Dimension(300, 150), true, true)

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.simpleName + ")", ex)
            Log.info("PipelineManager", "Make sure your pipeline implements a public constructor with no parameters or with a Telemetry parameter")

            eocvSim.visualizer.pipelineSelector.selectedIndex = currentPipelineIndex

            Log.blank()
        } catch (ex: Exception) {
            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Falling back to previous one",
                    "Close", Dimension(300, 150), true, true)

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.simpleName + ")", ex)
            Log.blank()

            eocvSim.visualizer.pipelineSelector.selectedIndex = currentPipelineIndex

            return
        }

        currentPipeline      = nextPipeline
        currentTelemetry     = nextTelemetry
        currentPipelineIndex = index
        currentPipelineName  = currentPipeline!!.javaClass.simpleName

        hasInitCurrentPipeline = false

        currentPipelineContext?.close()
        currentPipelineContext = newSingleThreadContext("Pipeline-$currentPipelineName")

        activePipelineContexts.add(currentPipelineContext!!)

        eocvSim.visualizer.pipelineSelector.selectedIndex = currentPipelineIndex

        //if pause on images option is turned on by user
        if (eocvSim.configManager.config.pauseOnImages) {
            //pause next frame if current selected inputsource is an image
            eocvSim.inputSourceManager.pauseIfImageTwoFrames()
        }

        onPipelineChange.run()
    }

    /**
     * Change to the requested pipeline only if we're
     * not in the requested pipeline right now.
     */
    fun changePipeline(index: Int) {
        if (index == currentPipelineIndex) return
        forceChangePipeline(index)
    }

    fun requestChangePipeline(index: Int) = onUpdate.doOnce { changePipeline(index) }

    fun requestForceChangePipeline(index: Int) = onUpdate.doOnce { forceChangePipeline(index) }

    fun runThenPause() {
        setPaused(false)
        eocvSim.onMainUpdate.doOnce { setPaused(true) }
    }

    fun setPaused(paused: Boolean, pauseReason: PauseReason) {
        this.paused = paused

        if (this.paused) {
            this.pauseReason = pauseReason
            onPause.run()
        } else {
            this.pauseReason = PauseReason.NOT_PAUSED
            onResume.run()
        }

        eocvSim.visualizer.pipelineSelector.pipelinePauseBtt.isSelected = this.paused
    }

    fun togglePause() = setPaused(!paused)

    @JvmOverloads
    fun requestSetPaused(paused: Boolean, pauseReason: PauseReason = PauseReason.USER_REQUESTED) {
        eocvSim.onMainUpdate.doOnce { setPaused(paused, pauseReason) }
    }

    fun setPaused(paused: Boolean) {
        setPaused(paused, PauseReason.USER_REQUESTED)
    }

}