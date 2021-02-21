package com.github.serivesmejia.eocvsim.gui.component.tuner.types

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.tuner.TunableField
import com.github.serivesmejia.eocvsim.util.event.KEventListener
import com.qualcomm.robotcore.util.Range
import javax.swing.JSlider
import kotlin.math.roundToInt

class TunableSlider(val index: Int,
                    val tunableField: TunableField<*>,
                    val eocvSim: EOCVSim,
                    minBound: Int = 0,
                    maxBound: Int = 255) : JSlider() {

    var inControl = false

    constructor(i: Int, tunableField: TunableField<Any>, eocvSim: EOCVSim) : this(i, tunableField, eocvSim, 0, 255)

    private val changeFieldValue = KEventListener {
        if(inControl) {
            tunableField.setGuiFieldValue(index, value.toString())

            if (eocvSim.pipelineManager.paused)
                eocvSim.pipelineManager.setPaused(false)
        }
    }

    init {
        minimum = minBound
        maximum = maxBound

        setValue(tunableField.getGuiFieldValue(index))

        addChangeListener {
            eocvSim.onMainUpdate.doOnce(changeFieldValue)
        }

        tunableField.onValueChange.doPersistent {
            if (!inControl) {
                setValue(tunableField.getGuiFieldValue(index))
            }
        }
    }

    fun setValue(value: Any) {
        val newValue = if(value is String) {
            try {
                value.toDouble()
            } catch(ignored: NumberFormatException) {
                0.0
            }
        } else {
            try {
                value.toString().toDouble()
            } catch(ignored: NumberFormatException) {
                0.0
            }
        }

        this.value = Range.clip(newValue.roundToInt(), minimum, maximum)
    }

}