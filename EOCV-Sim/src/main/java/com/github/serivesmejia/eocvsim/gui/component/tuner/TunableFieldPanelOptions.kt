package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.Icons
import com.github.serivesmejia.eocvsim.gui.component.PopupX
import com.github.serivesmejia.eocvsim.util.extension.CvExt.cvtColor
import com.github.serivesmejia.eocvsim.util.extension.NumberExt.clipUpperZero
import com.qualcomm.robotcore.util.Range
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

class TunableFieldPanelOptions(val fieldPanel: TunableFieldPanel) : JPanel() {

    companion object {
        //getting resized icons from statically loaded ones
        private val sliderIco    = Icons.getImageResized("ico_slider", 15, 15)
        private val textBoxIco   = Icons.getImageResized("ico_textbox", 15, 15)
        private val configIco    = Icons.getImageResized("ico_config", 15, 15)
        private val colorPickIco = Icons.getImageResized("ico_colorpick", 15, 15)
    }

    val textBoxSliderToggle = JToggleButton()
    val configButton        = JButton()
    val colorPickButton     = JToggleButton()

    val configPanel = TunableFieldPanelConfig(this, Size(0.0, 255.0), TunableFieldPanelConfig.PickerColorSpace.HSV)

    //toggle between textbox and slider ico,
    //and adding and removing config button
    var mode = TunableFieldPanel.Mode.TEXTBOXES
        set(value) {
            when(value) {
                TunableFieldPanel.Mode.SLIDERS -> {
                    textBoxSliderToggle.icon = sliderIco
                    textBoxSliderToggle.isSelected = true
                }
                TunableFieldPanel.Mode.TEXTBOXES -> {
                    textBoxSliderToggle.icon = textBoxIco
                    textBoxSliderToggle.isSelected = false
                }
            }

            handleResize()

            if(fieldPanel.mode != value) fieldPanel.mode = value
            field = value
        }

    init {
        //set initial icon for buttons
        textBoxSliderToggle.icon = sliderIco
        configButton.icon        = configIco
        colorPickButton.icon     = colorPickIco

        add(textBoxSliderToggle)
        add(configButton)
        add(colorPickButton)

        textBoxSliderToggle.addActionListener {
            if(textBoxSliderToggle.isSelected) {
                mode = TunableFieldPanel.Mode.SLIDERS
            } else {
                mode = TunableFieldPanel.Mode.TEXTBOXES
            }
        }

        configButton.addActionListener {
            val configLocation = configButton.locationOnScreen
            val configHeight   = configButton.height + configPanel.height / 2

            val window = SwingUtilities.getWindowAncestor(this)
            val popup  = PopupX(window, configPanel, configLocation.x, configLocation.y - configHeight)

            popup.show()
            configPanel.attachOnceToPopup(popup)
        }

        colorPickButton.addActionListener {
            val colorPicker = fieldPanel.tunableField.eocvSim.visualizer.colorPicker

            //start picking if global color picker is not being used by other panel
            if(!colorPicker.isPicking && colorPickButton.isSelected) {
                startPicking(colorPicker)
            } else { //handles cases when cancelling picking
                colorPicker.stopPicking()
                //if we weren't the ones controlling the last picking,
                //start picking again to gain control for this panel
                if(colorPickButton.isSelected) startPicking(colorPicker)
            }
        }

        fieldPanel.addComponentListener(object: ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) = handleResize()
        })

        addAncestorListener(object: AncestorListener {
            override fun ancestorRemoved(event: AncestorEvent?) {}
            override fun ancestorMoved(event: AncestorEvent?) {}

            override fun ancestorAdded(event: AncestorEvent?) = handleResize()
        })
    }

    private fun startPicking(colorPicker: ColorPicker) {
        //when user picks a color
        colorPicker.onPick.doOnce {
            val colorScalar = colorPicker.colorRgb.cvtColor(configPanel.pickerColorSpace!!.cvtCode)

            //setting the scalar value in order from first to fourth field
            for(i in 0 .. (fieldPanel.fields.size - 1).clipUpperZero()) {
                //if we're still in range of the scalar values amount
                if(i < colorScalar.`val`.size) {
                    val colorVal = colorScalar.`val`[i]

                    fieldPanel.setFieldValue(i, colorVal)
                    fieldPanel.tunableField.setGuiFieldValue(i, colorVal.toString())
                } else { break } //keep looping until we write the entire scalar value
            }
            colorPickButton.isSelected = false
        }

        //handles cancel cases, mostly when passing control to another panel
        colorPicker.onCancel.doOnce { colorPickButton.isSelected = false }

        //might want to start picking to this panel here...
        colorPicker.startPicking()
    }

    //handling resizes for responsive buttons arrangement
    private fun handleResize() {
        val buttonsHeight = textBoxSliderToggle.height + colorPickButton.height + configButton.height

        layout = if(fieldPanel.height > buttonsHeight && mode == TunableFieldPanel.Mode.SLIDERS) {
            GridLayout(3, 1)
        } else {
            FlowLayout()
        }

        revalAndRepaint()
    }

    private fun revalAndRepaint() {
        textBoxSliderToggle.revalidate()
        textBoxSliderToggle.repaint()

        revalidate()
        repaint()
    }

}