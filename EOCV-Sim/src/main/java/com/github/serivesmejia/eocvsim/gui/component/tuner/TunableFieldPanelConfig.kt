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
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

class TunableFieldPanelConfig(private val fieldOptions: TunableFieldPanelOptions,
                              initialSliderRange: Size,
                              initialPickerColorSpace: PickerColorSpace) : JPanel() {

    private val sliderRangeFields     = SizeFields(initialSliderRange, allowsDecimals, true,"Slider range:", " to ")
    private val colorSpaceComboBox    = EnumComboBox("Color space: ", PickerColorSpace::class.java, PickerColorSpace.values())

    private val applyToAllButtonPanel = JPanel(GridBagLayout())
    private val applyToAllButton      = JButton("Apply to all")

    val config = Config(initialSliderRange, initialPickerColorSpace)

    private val allowsDecimals
        get() = fieldOptions.fieldPanel.tunableField.allowMode == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL

    enum class PickerColorSpace(val cvtCode: Int) {
        YCrCb(Imgproc.COLOR_RGB2YCrCb),
        HSV(Imgproc.COLOR_RGB2HSV),
        RGB(Imgproc.COLOR_RGBA2RGB),
        Lab(Imgproc.COLOR_RGB2Lab)
    }

    data class Config(var sliderRange: Size,
                      var pickerColorSpace: PickerColorSpace)

    init {
        layout = GridLayout(3, 1)

        sliderRangeFields.onChange.doPersistent {
            if(sliderRangeFields.valid) {
                try {
                    config.sliderRange = sliderRangeFields.currentSize
                } catch(ignored: NumberFormatException) {}
            }
        }
        add(sliderRangeFields)

        colorSpaceComboBox.selectedEnum = initialPickerColorSpace
        add(colorSpaceComboBox)

        val constCenter    = GridBagConstraints()
        constCenter.anchor = GridBagConstraints.CENTER
        constCenter.fill   = GridBagConstraints.HORIZONTAL

        applyToAllButtonPanel.add(applyToAllButton, constCenter)
        add(applyToAllButtonPanel)

        validate()
        updateUI()
    }

    fun attachOnceToPopup(popup: PopupX) {
        popup.onShow.doOnce {
            sliderRangeFields.widthTextField.text = config.sliderRange.width.toString()
            sliderRangeFields.heightTextField.text = config.sliderRange.height.toString()
            colorSpaceComboBox.selectedEnum = config.pickerColorSpace
        }

        //set the slider bounds when the popup gets closed
        popup.onHide.doOnce {
            //if user entered a valid number and our max value is bigger than the minimum...
            if(sliderRangeFields.valid && config.sliderRange.height > config.sliderRange.width) {
                fieldOptions.fieldPanel.setSlidersRange(config.sliderRange.width, config.sliderRange.height)
            }
        }
    }

}