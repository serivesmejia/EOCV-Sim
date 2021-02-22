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
                    minBound: Double = 0.0,
                    maxBound: Double = 255.0) : JSlider() {

    var inControl = false

    var scale = 10

    constructor(i: Int, tunableField: TunableField<Any>, eocvSim: EOCVSim, valueLabel: JLabel) : this(i, tunableField, eocvSim, valueLabel,0.0, 255.0)

    private val changeFieldValue = KEventListener {
        if(inControl) {
            tunableField.setGuiFieldValue(index, calculateValue().toString())

            if (eocvSim.pipelineManager.paused)
                eocvSim.pipelineManager.setPaused(false)
        }
    }

    init {
        setMajorTickSpacing(scale)
        setMinorTickSpacing(scale / 4)

        setBounds(minBound, maxBound)

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
        val newValue = try {
            value.toString().toDouble()
        } catch(ignored: NumberFormatException) {
            0.0
        }

        this.value = Range.clip(newValue * scale, minimum.toDouble(), maximum.toDouble()).roundToInt()
    }

    fun calculateValue() = value.toDouble() / scale.toDouble()

    fun setBounds(minBound: Double, maxBound: Double) {
        minimum = (minBound * scale).roundToInt()
        maximum = (maxBound * scale).roundToInt()
    }

}