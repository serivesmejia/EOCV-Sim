package com.github.serivesmejia.eocvsim.gui.component.tuner.types

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.tuner.TunableField
import com.github.serivesmejia.eocvsim.util.event.KEventListener
import com.qualcomm.robotcore.util.Range
import javax.swing.JLabel
import javax.swing.JSlider
import kotlin.math.abs
import kotlin.math.roundToInt

class TunableSlider(val index: Int,
                    val tunableField: TunableField<*>,
                    val eocvSim: EOCVSim,
                    val valueLabel: JLabel,
                    var minBound: Double = 0.0,
                    var maxBound: Double = 255.0) : JSlider() {

    var inControl = false

    constructor(i: Int, tunableField: TunableField<Any>, eocvSim: EOCVSim, valueLabel: JLabel) : this(i, tunableField, eocvSim, valueLabel,0.0, 255.0)

    private val changeFieldValue = KEventListener {
        if(inControl) {
            tunableField.setGuiFieldValue(index, value.toString())

            if (eocvSim.pipelineManager.paused)
                eocvSim.pipelineManager.setPaused(false)
        }
    }

    init {
        minimum = 0
        maximum = 1000

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
        var newValue = if(value is String) {
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

        newValue = Range.clip(newValue, minBound, maxBound)

        if(minBound < 0 && maxBound >= 0) {
            val scale = abs(newValue) / maxBound

            this.value = when {
                newValue < 0.0 ->
                    Range.clip((maximum.toDouble() / 2.0) * scale, minimum.toDouble(), maximum.toDouble()).roundToInt()
                newValue > 0.0 ->
                    Range.clip(maximum.toDouble() * scale, minimum.toDouble(), maximum.toDouble()).roundToInt()
                else -> 500
            }
        } else {
            val scale = newValue / maxBound
            this.value = Range.clip(maximum.toDouble() * scale, minimum.toDouble(), maximum.toDouble()).roundToInt()
        }

    }

    fun calculateValue(): Double {
        val halfMax = (maximum / 2)

        if(minBound < 0 && maxBound >= 0) {
            val newValue = when {
                value < halfMax -> {
                    val scale = value / halfMax
                    
                }
                value > halfMax -> {
                    val scale = (value - halfMax) / halfMax

                }
                else -> 0.0
            }

            return Range.clip(maxBound * scale.toDouble(), minBound, maxBound)
        } else {

        }
    }

}