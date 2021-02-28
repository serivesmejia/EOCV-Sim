package com.github.serivesmejia.eocvsim.gui.component

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.gui.DialogFactory
import com.github.serivesmejia.eocvsim.gui.component.input.EnumComboBox
import com.github.serivesmejia.eocvsim.input.SourceType
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

class CreateSourcePanel(eocvSim: EOCVSim) : JPanel(GridLayout(2, 1)) {

    private val sourceSelectComboBox = EnumComboBox("", SourceType::class.java, SourceType.values())
    private val sourceSelectPanel    = JPanel(FlowLayout(FlowLayout.CENTER))

    private val nextButton = JButton("Next")
    private val nextPanel  = JPanel()

    var popup: PopupX? = null

    init {
        sourceSelectComboBox.removeEnumOption(SourceType.UNKNOWN) //removes the UNKNOWN enum
        sourceSelectPanel.add(sourceSelectComboBox) //add to separate panel to center element
        add(sourceSelectPanel) //add centered panel to this

        nextButton.addActionListener {
            //creates "create source" dialog from selected enum
            DialogFactory.createSourceDialog(eocvSim, sourceSelectComboBox.selectedEnum!!)
            popup?.hide()
        }
        nextPanel.add(nextButton)

        add(nextPanel)
    }

}