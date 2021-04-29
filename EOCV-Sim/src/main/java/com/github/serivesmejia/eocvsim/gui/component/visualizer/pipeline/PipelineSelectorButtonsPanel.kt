package com.github.serivesmejia.eocvsim.gui.component.visualizer.pipeline

import com.github.serivesmejia.eocvsim.EOCVSim
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JToggleButton

class PipelineSelectorButtonsPanel(eocvSim: EOCVSim) : JPanel(GridBagLayout()) {

    val pipelinePauseBtt         = JToggleButton("Pause")
    val pipelineRecordBtt        = JToggleButton("Record")

    val pipelineWorkspaceBtt = JButton("Workspace")

    init {
        //listener for changing pause state
        pipelinePauseBtt.addActionListener {
            eocvSim.onMainUpdate.doOnce { eocvSim.pipelineManager.setPaused(pipelinePauseBtt.isSelected) }
        }

        pipelinePauseBtt.addChangeListener {
            pipelinePauseBtt.text = if(pipelinePauseBtt.isSelected) "Resume" else "Pause"
        }
        add(pipelinePauseBtt, GridBagConstraints())

        pipelineRecordBtt.addActionListener {
            eocvSim.onMainUpdate.doOnce {
                if (pipelineRecordBtt.isSelected) {
                    if (!eocvSim.isCurrentlyRecording()) eocvSim.startRecordingSession()
                } else {
                    if (eocvSim.isCurrentlyRecording()) eocvSim.stopRecordingSession()
                }
            }
        }
        add(pipelineRecordBtt, GridBagConstraints().apply { gridx = 1 })

        add(pipelineWorkspaceBtt, GridBagConstraints().apply {
            gridy = 1
            weighty = 0.5
            fill = GridBagConstraints.HORIZONTAL
        })
    }

}