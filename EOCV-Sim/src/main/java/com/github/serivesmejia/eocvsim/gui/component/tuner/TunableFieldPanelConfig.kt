package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.Icons.getImageResized
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JToggleButton

class TunableFieldPanelConfig(private val fieldPanel: TunableFieldPanel) : JPanel() {

    companion object {
        private val sliderIco = getImageResized("ico_slider", 15, 15)
        private val textBoxIco = getImageResized("ico_textbox", 15, 15)
        private val configIco = getImageResized("ico_config", 15, 15)
    }

    val textBoxSliderToggle = JToggleButton()
    val configButton = JButton()

    init {
        textBoxSliderToggle.icon = sliderIco
        configButton.icon = configIco

        add(textBoxSliderToggle)

        textBoxSliderToggle.addActionListener {
            if(textBoxSliderToggle.isSelected) {
                textBoxSliderToggle.icon = textBoxIco
                add(configButton)
            } else {
                textBoxSliderToggle.icon = sliderIco
                remove(configButton)
            }

            revalAndRepaint()
        }
    }

    private fun revalAndRepaint() {
        textBoxSliderToggle.revalidate()
        textBoxSliderToggle.repaint()

        revalidate()
        repaint()
    }

}