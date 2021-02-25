package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.component.input.EnumComboBox
import com.github.serivesmejia.eocvsim.gui.component.input.SizeFields
import com.github.serivesmejia.eocvsim.tuner.TunableField
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel

class TunableFieldPanelConfig(private val fieldOptions: TunableFieldPanelOptions,
                              private val initialSliderRange: Size,
                              private val initialPickerColorSpace: PickerColorSpace) : JPanel() {

    val sliderRangeFields = SizeFields(initialSliderRange, allowsDecimals, "Slider range:", " to ")
    val colorSpaceComboBox = EnumComboBox("Color space: ", PickerColorSpace::class.java, PickerColorSpace.values())

    var sliderRange = initialSliderRange
        private set
    val pickerColorSpace: PickerColorSpace?
        get() = colorSpaceComboBox.selectedEnum

    val allowsDecimals
        get() = fieldOptions.fieldPanel.tunableField.allowMode == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL

    enum class PickerColorSpace(val cvtCode: Int) {
        YCrCb(Imgproc.COLOR_RGB2YCrCb),
        HSV(Imgproc.COLOR_RGB2HSV),
        RGB(Imgproc.COLOR_RGBA2RGB),
        Lab(Imgproc.COLOR_RGB2Lab)
    }

    init {
        layout = GridLayout(2, 1)
        add(sliderRangeFields)

        colorSpaceComboBox.selectedEnum = initialPickerColorSpace
        add(colorSpaceComboBox)
    }

}