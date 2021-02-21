package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.Icons
import com.github.serivesmejia.eocvsim.gui.component.PopupX
import javax.swing.*

class TunableFieldPanelOptions(private val fieldPanel: TunableFieldPanel) : JPanel() {

    companion object {
        //getting resized icons from statically loaded ones
        private val sliderIco    = Icons.getImageResized("ico_slider", 15, 15)
        private val textBoxIco   = Icons.getImageResized("ico_textbox", 15, 15)
        private val configIco    = Icons.getImageResized("ico_config", 15, 15)
        private val colorPickIco = Icons.getImageResized("ico_colorpick", 15, 15)
    }



    val textBoxSliderToggle = JToggleButton()
    val configButton        = JButton()
    val colorPickButton     = JButton()

    val configPanel = TunableFieldPanelConfig(this)

    init {
        //set initial icon for buttons
        textBoxSliderToggle.icon = sliderIco
        configButton.icon        = configIco
        colorPickButton.icon     = colorPickIco

        add(textBoxSliderToggle)
        add(colorPickButton)

        textBoxSliderToggle.addActionListener {
            //toggle between textbox and slider ico,
            //and adding and removing config button
            if(textBoxSliderToggle.isSelected) {
                fieldPanel.setMode(TunableFieldPanel.Mode.SLIDERS)

                textBoxSliderToggle.icon = sliderIco

                //removes & adds the color picker button when adding th config button
                //so that it stays in the same position after adding config button
                remove(colorPickButton)
                add(configButton)
                add(colorPickButton)
            } else {
                fieldPanel.setMode(TunableFieldPanel.Mode.TEXTBOXES)

                textBoxSliderToggle.icon = textBoxIco
                remove(configButton)
            }

            revalAndRepaint()
        }

        configButton.addActionListener {
            val configLocation = configButton.locationOnScreen
            val configHeight   = configButton.height

            val window = SwingUtilities.getWindowAncestor(this)
            val popup  = PopupX(window, configPanel, configLocation.x, configLocation.y - configHeight)

            popup.show()
        }
    }

    private fun revalAndRepaint() {
        textBoxSliderToggle.revalidate()
        textBoxSliderToggle.repaint()

        revalidate()
        repaint()
    }

}