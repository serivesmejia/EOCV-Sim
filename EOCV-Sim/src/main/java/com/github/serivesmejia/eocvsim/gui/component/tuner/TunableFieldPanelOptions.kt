package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.Icons
import com.github.serivesmejia.eocvsim.gui.component.PopupX
import org.opencv.core.Size
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

    val sliderRange = Size(0.0, 255.0)

    val textBoxSliderToggle = JToggleButton()
    val configButton        = JButton()
    val colorPickButton     = JButton()

    val configPanel = TunableFieldPanelConfig(this, sliderRange)

    //toggle between textbox and slider ico,
    //and adding and removing config button
    var mode = TunableFieldPanel.Mode.TEXTBOXES
        set(value) {
            when(value) {
                TunableFieldPanel.Mode.SLIDERS -> {
                    textBoxSliderToggle.icon = sliderIco
                    textBoxSliderToggle.isSelected = true

                    //removes & adds the color picker button when adding th config button
                    //so that it stays in the same position after adding config button
                    remove(colorPickButton)
                    add(configButton)
                    add(colorPickButton)
                }
                TunableFieldPanel.Mode.TEXTBOXES -> {
                    textBoxSliderToggle.icon = textBoxIco
                    textBoxSliderToggle.isSelected = false

                    remove(configButton)
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
        }

        colorPickButton.addActionListener {
            val colorPicker = fieldPanel.tunableField.eocvSim.visualizer.colorPicker

            if(!colorPicker.isPicking) {
                colorPicker.onPick.doOnce {
                    println("onPick ${colorPicker.colorRgb}")
                    for(i in 0..fieldPanel.fields.size) {
                        try {
                            val colorVal = colorPicker.colorRgb.`val`[i]
                            fieldPanel.setFieldValue(i, colorVal)
                        } catch(ignored: ArrayIndexOutOfBoundsException) { break }
                    }
                }

                println("start pick")
                colorPicker.startPicking()
            } else {
                println("stop pick")
                colorPicker.stopPicking()
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