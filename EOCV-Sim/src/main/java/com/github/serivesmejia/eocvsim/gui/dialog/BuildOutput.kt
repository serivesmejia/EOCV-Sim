package com.github.serivesmejia.eocvsim.gui.dialog

import com.github.serivesmejia.eocvsim.EOCVSim
import java.awt.*
import javax.swing.*

class BuildOutput(parent: JFrame, buildOutputMessage: String, eocvSim: EOCVSim) {

    private val buildOutput = JDialog(parent)

    init {
        eocvSim.visualizer.childDialogs.add(buildOutput)

        buildOutput.isModal = true
        buildOutput.title = "Build output"
        buildOutput.setSize(500, 350)

        buildOutput.contentPane.layout = BoxLayout(buildOutput.contentPane, BoxLayout.Y_AXIS)

        val buildOutputArea = JTextArea(buildOutputMessage)
        buildOutputArea.isEditable = false
        buildOutputArea.lineWrap   = true

        val buildOutputScroll = JScrollPane(buildOutputArea)
        buildOutputScroll.verticalScrollBarPolicy   = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        buildOutputScroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS

        val buildOutputScrollPanel = JPanel(GridLayout(1, 1)).apply {
            add(buildOutputScroll)
        }

        buildOutput.contentPane.add(buildOutputScrollPanel)

        val bottomButtonsPanel = JPanel(GridLayout(1, 2))

        val clearButton = JButton("Clear")

        clearButton.addActionListener {
            buildOutputArea.text = ""
        }
        bottomButtonsPanel.add(clearButton)

        val compileButton = JButton("Compile")
        bottomButtonsPanel.add(compileButton)

        buildOutput.contentPane.add(bottomButtonsPanel)

        buildOutput.setLocationRelativeTo(null)
        buildOutput.isVisible = true
    }

}