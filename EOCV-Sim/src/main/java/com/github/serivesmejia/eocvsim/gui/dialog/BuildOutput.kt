package com.github.serivesmejia.eocvsim.gui.dialog

import com.github.serivesmejia.eocvsim.EOCVSim
import java.awt.*
import javax.swing.*

class BuildOutput(parent: JFrame, buildOutputMessage: String, eocvSim: EOCVSim) {

    private val buildOutput = JDialog(parent)

    private val bottomButtonsPanel = JPanel()

    init {
        eocvSim.visualizer.childDialogs.add(buildOutput)

        buildOutput.isModal = true
        buildOutput.title = "Build output"
        buildOutput.setSize(500, 350)

        buildOutput.contentPane.layout = GridBagLayout()

        val buildOutputArea = JTextArea(buildOutputMessage)
        buildOutputArea.isEditable = false
        buildOutputArea.lineWrap   = true

        val buildOutputScroll = JScrollPane(buildOutputArea)
        buildOutputScroll.verticalScrollBarPolicy   = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        buildOutputScroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS

        buildOutput.contentPane.add(buildOutputScroll, GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            weightx = 0.5
            weighty = 1.0
        })

        bottomButtonsPanel.layout = BoxLayout(bottomButtonsPanel, BoxLayout.LINE_AXIS)

        bottomButtonsPanel.add(Box.createRigidArea(Dimension(4, 0)))

        val compileButton = JButton("Compile")
        bottomButtonsPanel.add(compileButton)

        bottomButtonsPanel.add(Box.createHorizontalGlue())

        val clearButton = JButton("Clear")

        clearButton.addActionListener {
            buildOutputArea.text = ""
        }
        bottomButtonsPanel.add(clearButton)

        bottomButtonsPanel.add(Box.createRigidArea(Dimension(4, 0)))

        val closeButton = JButton("Close")

        closeButton.addActionListener {
            buildOutput.isVisible = false
        }
        bottomButtonsPanel.add(closeButton)

        bottomButtonsPanel.add(Box.createRigidArea(Dimension(4, 0)))

        buildOutput.contentPane.add(bottomButtonsPanel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridy = 1

            weightx = 1.0
            ipadx   = 10
            ipady   = 10
        })

        buildOutput.setLocationRelativeTo(null)
        buildOutput.isVisible = true
    }

}