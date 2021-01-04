package com.github.serivesmejia.eocvsim.pipeline

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Mat
import org.openftc.easyopencv.OpenCvPipeline
import java.awt.Dimension
import java.lang.reflect.Constructor
import java.util.*

class PipelineManager(var eocvSim: EOCVSim) {

    @JvmField val onUpdate = EventHandler("OnPipelineUpdate")
    @JvmField val onPipelineChange = EventHandler("OnPipelineChange")
    @JvmField val onPause = EventHandler("OnPipelinePause")
    @JvmField val onResume = EventHandler("OnPipelineResume")

    val pipelines = ArrayList<Class<out OpenCvPipeline>>()

    var currentPipeline: OpenCvPipeline? = null
        private set
    var currentPipelineName = ""
        private set
    var currentPipelineIndex = -1
        private set

    var currentTelemetry: Telemetry? = null
        private set

    @Volatile var lastOutputMat: Mat? = null
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
        Log.white()

        lastOutputMat = Mat()
        requestChangePipeline(0) //change to the default pipeline

    }

    fun addPipelineClass(C: Class<*>) {
        try {
            pipelines.add(C as Class<out OpenCvPipeline>)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.error("PipelineManager", "Unable to cast " + C.name + " to OpenCvPipeline class.")
            Log.error("PipelineManager", "Remember that the pipeline class should extend OpenCvPipeline")
        }
    }

    fun update(inputMat: Mat) {

        onUpdate.run()

        if (paused) {
            if (lastOutputMat == null || lastOutputMat!!.empty()) lastOutputMat = inputMat
            return
        }

        lastOutputMat = if (currentPipeline != null) {
            currentPipeline!!.processFrame(inputMat)
        } else {
            inputMat
        }

    }

    fun changePipeline(index: Int) {

        if (index == currentPipelineIndex) return

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
            nextPipeline!!.init(eocvSim.inputSourceManager.lastMatFromSource)

        } catch (ex: NoSuchMethodException) {

            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Check console for details",
                    "Close", Dimension(300, 150), true, true)

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.simpleName + ")", ex)
            Log.info("PipelineManager", "Make sure your pipeline implements a public constructor with no parameters or with a Telemetry parameter")

            Log.white()

        } catch (ex: Exception) {

            eocvSim.visualizer.asyncPleaseWaitDialog("Error while initializing requested pipeline", "Falling back to previous one",
                    "Close", Dimension(300, 150), true, true)

            Log.error("PipelineManager", "Error while initializing requested pipeline (" + pipelineClass.simpleName + ")", ex)
            Log.white()

            eocvSim.visualizer.pipelineSelector.selectedIndex = currentPipelineIndex

            return

        }

        Log.info("PipelineManager", "Initialized pipeline " + pipelineClass.name)
        Log.white()

        currentPipeline = nextPipeline
        currentTelemetry = nextTelemetry
        currentPipelineIndex = index
        currentPipelineName = currentPipeline!!.javaClass.simpleName

        //if pause on images option is turned on by user
        if (eocvSim.configManager.config.pauseOnImages) eocvSim.inputSourceManager.pauseIfImageTwoFrames() //pause next frame if current selected inputsource is an image

        onPipelineChange.run()

    }

    fun requestChangePipeline(index: Int) {
        onUpdate.doOnce { changePipeline(index) }
    }

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

        eocvSim.visualizer.pipelinePauseBtt.isSelected = this.paused

    }

    fun togglePause() {
        setPaused(!paused)
    }

    @JvmOverloads
    fun requestSetPaused(paused: Boolean, pauseReason: PauseReason = PauseReason.USER_REQUESTED) {
        eocvSim.onMainUpdate.doOnce { setPaused(paused, pauseReason) }
    }

    fun setPaused(paused: Boolean) {
        setPaused(paused, PauseReason.USER_REQUESTED)
    }

}