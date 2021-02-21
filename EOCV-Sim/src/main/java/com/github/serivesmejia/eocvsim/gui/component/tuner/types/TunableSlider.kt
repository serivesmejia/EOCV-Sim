package com.github.serivesmejia.eocvsim.gui.component.tuner.types

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.tuner.TunableField
import com.qualcomm.robotcore.util.Range
import javax.swing.JSlider

class TunableSlider(val index: Int,
                    val tunableField: TunableField<*>,
                    val eocvSim: EOCVSim,
                    minBound: Int = 0,
                    maxBound: Int = 255) : JSlider() {

    var inControl = false

    constructor(i: Int, tunableField: TunableField<Any>, eocvSim: EOCVSim) : this(i, tunableField, eocvSim, 0, 255)

    init {
        minimum = minBound
        maximum = maxBound

        setValue(tunableField.getGuiFieldValue(index))

        addChangeListener {
            eocvSim.onMainUpdate.doOnce {
                tunableField.setGuiFieldValue(index, value.toString())

                if (eocvSim.pipelineManager.paused) {
                    eocvSim.pipelineManager.setPaused(false)
                }
            }
        }

        tunableField.onValueChange.doPersistent(Runnable {
            if (!inControl) {
                setValue(tunableField.getGuiFieldValue(index))
            }
        })
    }

    fun setValue(value: Any) {
        val newValue = try {
            value as Int
        } catch(ignored: ClassCastException) {
            try {
                Integer.valueOf(value as String)
            } catch(ignored: ClassCastException) {
                0
            }
        }

        this.value = Range.clip(newValue, minimum, maximum)
        revalidate(); repaint()
    }

}