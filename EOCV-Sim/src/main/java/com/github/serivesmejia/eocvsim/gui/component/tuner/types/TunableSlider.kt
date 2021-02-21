package com.github.serivesmejia.eocvsim.gui.component.tuner.types

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.tuner.TunableField
import javax.swing.JSlider

class TunableSlider(val index: Int,
                    val tunableField: TunableField<*>,
                    val eocvSim: EOCVSim,
                    minBound: Int = 0,
                    maxBound: Int = 255) : JSlider() {

    constructor(i: Int, tunableField: TunableField<Any>, eocvSim: EOCVSim) : this(i, tunableField, eocvSim, 0, 255)

    init {
        minimum = minBound
        maximum = maxBound

        addChangeListener {
            eocvSim.onMainUpdate.doOnce {
                tunableField.setGuiFieldValue(index, value.toString())

                if (eocvSim.pipelineManager.paused) {
                    eocvSim.pipelineManager.setPaused(false)
                }
            }
        }
    }

}