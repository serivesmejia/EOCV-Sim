/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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