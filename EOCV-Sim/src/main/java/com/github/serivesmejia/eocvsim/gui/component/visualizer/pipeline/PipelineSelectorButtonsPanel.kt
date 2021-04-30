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