package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.component.input.EnumComboBox
import com.github.serivesmejia.eocvsim.gui.component.input.SizeFields
import com.github.serivesmejia.eocvsim.tuner.TunableField
import org.opencv.core.Size
import javax.swing.JLabel
import javax.swing.JPanel

class TunableFieldPanelConfig(private val fieldOptions: TunableFieldPanelOptions,
                              private val sliderRange: Size) : JPanel() {

    val sliderRangeFields = SizeFields(sliderRange, allowsDecimals, "Slider range:", " to ")
    val colorSpaceComboBox = EnumComboBox(PickerColorSpace::class.java, PickerColorSpace.values())

    val allowsDecimals
        get() = fieldOptions.fieldPanel.tunableField.allowMode == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL

    enum class PickerColorSpace { YCrCb, HSV, RGB, LAB }

    init {
        add(sliderRangeFields)
        add(colorSpaceComboBox)
    }

}