package com.github.serivesmejia.eocvsim.gui.component

import com.qualcomm.robotcore.util.Range
import javax.swing.JSlider
import kotlin.math.roundToInt

open class SliderX(private var minBound: Double,
                   private var maxBound: Double,
                   private val scale: Int) : JSlider() {

    var scaledValue: Double = 0.0
        set(value) {
            field = Range.clip(value * scale, minimum.toDouble(), maximum.toDouble())
            this.value = field.roundToInt()
        }
        get() {
            return Range.clip(this.value.toDouble() / scale, minBound, maxBound)
        }

    init {
        setScaledBounds(minBound, maxBound)
        setMajorTickSpacing(scale)
        setMinorTickSpacing(scale / 4)
    }

    fun setScaledBounds(minBound: Double, maxBound: Double) {
        this.minBound = minBound * scale
        this.maxBound = maxBound * scale

        minimum = this.minBound.roundToInt()
        maximum = this.maxBound.roundToInt()
    }

}