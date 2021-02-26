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

import com.github.serivesmejia.eocvsim.EOCVSim
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
import javax.swing.JToggleButton

class TunableFieldPanelConfig(private val fieldOptions: TunableFieldPanelOptions,
                              private val eocvSim: EOCVSim) : JPanel() {

    var config = eocvSim.config.globalTunableFieldsConfig.copy()
        private set

    var appliedSpecificConfig = false
        private set

    private val sliderRangeFieldsPanel = JPanel()

    private var sliderRangeFields     = createRangeFields()
    private val colorSpaceComboBox    = EnumComboBox("Color space: ", PickerColorSpace::class.java, PickerColorSpace.values())

    private val applyToAllButtonPanel = JPanel(GridBagLayout())
    private val applyToAllButton      = JToggleButton("Apply to all fields...")

    private val applyModesPanel             = JPanel(GridLayout(1, 2))
    private val applyToAllFieldsButton      = JButton("Globally")
    private val applyToAllOfSameTypeButton  = JButton("Of same type")

    private val constCenterBottom = GridBagConstraints()

    private val allowsDecimals
        get() = fieldOptions.fieldPanel.tunableField.allowMode == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL

    private val fieldTypeClass = fieldOptions.fieldPanel.tunableField::class.java

    //represents a color space conversion when picking from the viewport. always
    //convert from rgb to the desired color space since that's the color space of
    //the scalar the ColorPicker returns from the viewport after picking.
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

        //adding into an individual panel so that we can add
        //and remove later when recreating without much problem
        sliderRangeFieldsPanel.add(sliderRangeFields)
        add(sliderRangeFieldsPanel)

        //combo box to select color space
        colorSpaceComboBox.selectedEnum = config.pickerColorSpace
        add(colorSpaceComboBox)

        //centering apply to all button...
        val constCenter    = GridBagConstraints()
        constCenter.anchor = GridBagConstraints.CENTER
        constCenter.fill   = GridBagConstraints.HORIZONTAL
        constCenter.gridy  = 0

        //add apply to all button to a centered pane
        applyToAllButtonPanel.add(applyToAllButton, constCenter)
        add(applyToAllButtonPanel)

        //display or hide apply to all mode buttons
        applyToAllButton.addActionListener { toggleApplyModesPanel(applyToAllButton.isSelected) }

        //apply globally button and disable toggle for apply to all button
        applyToAllFieldsButton.addActionListener {
            toggleApplyModesPanel(false)
            applyGlobally()
        }
        applyModesPanel.add(applyToAllFieldsButton)

        //apply of same type button and disable toggle for apply to all button
        applyToAllOfSameTypeButton.addActionListener {
            toggleApplyModesPanel(false)
            applyOfSameType()
        }
        applyModesPanel.add(applyToAllOfSameTypeButton)

        //add two apply to all modes buttons to the bottom center
        constCenterBottom.anchor = GridBagConstraints.CENTER
        constCenterBottom.fill = GridBagConstraints.HORIZONTAL
        constCenterBottom.gridy = 1

        applyToAllButtonPanel.add(applyModesPanel, constCenterBottom)

        applyFromConfig()
    }

    //hides or displays apply to all mode buttons
    private fun toggleApplyModesPanel(show: Boolean) {
        if(show) {
            applyToAllButtonPanel.add(applyModesPanel, constCenterBottom)
        } else {
            applyToAllButtonPanel.remove(applyModesPanel)
        }

        //toggle or untoggle apply to all button
        applyToAllButton.isSelected = show

        //need to repaint...
        applyToAllButtonPanel.repaint(); applyToAllButtonPanel.revalidate()
        repaint(); revalidate()
    }

    private fun applyGlobally() {
        eocvSim.config.globalTunableFieldsConfig = config
    }

    private fun applyOfSameType() {
        val typeClass = fieldOptions.fieldPanel.tunableField::class.java
        eocvSim.config.specificTunableFieldConfig[typeClass.name] = config
    }

    //set the current config values and hide apply modes panel when panel show
    fun panelShow() {
        applyFromConfig()

        applyToAllButton.isSelected = false
        toggleApplyModesPanel(false)
    }

    //set the slider bounds when the popup gets closed
    fun panelHide() {
        applyToConfig()
        toggleApplyModesPanel(true)
    }

    //loads the config from global eocv sim config file
    fun applyFromConfig() {
        val specificConfigs = eocvSim.config.specificTunableFieldConfig

        //apply specific config if we have one, or else, apply global
        config = if(specificConfigs.containsKey(fieldTypeClass.name)) {
            appliedSpecificConfig = true
            specificConfigs[fieldTypeClass.name]!!
        } else {
            eocvSim.config.globalTunableFieldsConfig
        }

        updateGuiFromCurrentConfig()
    }

    //applies the current values to config
    fun applyToConfig() {
        //if user entered a valid number and our max value is bigger than the minimum...
        if(sliderRangeFields.valid) {
            config.sliderRange = sliderRangeFields.currentSize
            //update slider range in gui sliders...
            if(config.sliderRange.height > config.sliderRange.width)
                updateSlidersRange()
        }

        //set the color space enum to the config if it's not null
        colorSpaceComboBox.selectedEnum?.let {
            config.pickerColorSpace = it
        }
    }

    fun updateSlidersRange() = fieldOptions.fieldPanel.setSlidersRange(config.sliderRange.width, config.sliderRange.height)

    //updates the values displayed in this config's ui to the current config values
    private fun updateGuiFromCurrentConfig() {
        sliderRangeFieldsPanel.remove(sliderRangeFields) //remove old fields
        //need to recreate in order to set new values
        sliderRangeFields = createRangeFields()
        sliderRangeFieldsPanel.add(sliderRangeFields) //add new fields

        //need to reval&repaint as always
        sliderRangeFieldsPanel.revalidate(); sliderRangeFieldsPanel.repaint()

        colorSpaceComboBox.selectedEnum = config.pickerColorSpace
    }

    //simple short hand for a repetitive instantiation...
    private fun createRangeFields() = SizeFields(config.sliderRange, allowsDecimals, true,"Slider range:", " to ")

}