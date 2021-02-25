package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.component.PopupX
import com.github.serivesmejia.eocvsim.gui.component.input.EnumComboBox
import com.github.serivesmejia.eocvsim.gui.component.input.SizeFields
import com.github.serivesmejia.eocvsim.tuner.TunableField
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.GridLayout
import javax.swing.JPanel

class TunableFieldPanelConfig(private val fieldOptions: TunableFieldPanelOptions,
                              initialSliderRange: Size,
                              initialPickerColorSpace: PickerColorSpace) : JPanel() {

    val sliderRangeFields = SizeFields(initialSliderRange, allowsDecimals, true,"Slider range:", " to ")
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

        sliderRangeFields.onChange.doPersistent {
            if(sliderRangeFields.valid) {
                try {
                    sliderRange = sliderRangeFields.currentSize
                } catch(ignored: NumberFormatException) {}
            }
        }

        colorSpaceComboBox.selectedEnum = initialPickerColorSpace
        add(colorSpaceComboBox)
    }

    fun attachOnceToPopup(popup: PopupX) {
        //set the slider bounds when the popup gets closed
        popup.onHide.doOnce {
            //if user entered a valid number
            if(sliderRangeFields.valid) {
                //if our max value is bigger than the minimum...
                if(sliderRange.height > sliderRange.width) {
                    fieldOptions.fieldPanel.setSlidersRange(sliderRange.width, sliderRange.height)
                }
            }
        }
    }

}