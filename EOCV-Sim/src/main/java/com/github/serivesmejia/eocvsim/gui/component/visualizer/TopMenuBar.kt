
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

package com.github.serivesmejia.eocvsim.gui.component.visualizer

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.gui.DialogFactory
import com.github.serivesmejia.eocvsim.gui.Visualizer
import com.github.serivesmejia.eocvsim.gui.dialog.BuildOutput
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil
import com.github.serivesmejia.eocvsim.input.SourceType
import com.github.serivesmejia.eocvsim.workspace.util.VSCodeLauncher
import java.awt.Desktop
import java.net.URI
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class TopMenuBar(visualizer: Visualizer, eocvSim: EOCVSim) : JMenuBar() {

    @JvmField val mFileMenu = JMenu("File")
    @JvmField val mEditMenu = JMenu("Edit")
    @JvmField val mHelpMenu = JMenu("Help")

    @JvmField val fileWorkspCompile = JMenuItem("Build java files")

    init {
        val fileNewSubmenu = JMenu("New")
        mFileMenu.add(fileNewSubmenu)

        val fileNewInputSourceSubmenu = JMenu("Input Source")
        fileNewSubmenu.add(fileNewInputSourceSubmenu)

        //add all input source types to top bar menu
        for (type in SourceType.values()) {
            if (type == SourceType.UNKNOWN) continue //exclude unknown type

            val fileNewInputSourceItem = JMenuItem(type.coolName)

            fileNewInputSourceItem.addActionListener {
                DialogFactory.createSourceDialog(eocvSim, type)
            }

            fileNewInputSourceSubmenu.add(fileNewInputSourceItem)
        }

        val fileWorkspace = JMenu("Workspace")

        val fileWorkspSetWorkspace = JMenuItem("Select workspace")

        fileWorkspSetWorkspace.addActionListener { visualizer.selectPipelinesWorkspace() }
        fileWorkspace.add(fileWorkspSetWorkspace)

        fileWorkspCompile.addActionListener { visualizer.asyncCompilePipelines() }
        fileWorkspace.add(fileWorkspCompile)

        val fileWorkspBuildOutput = JMenuItem("Output")

        fileWorkspBuildOutput.addActionListener {
            if(!BuildOutput.isAlreadyOpened)
                DialogFactory.createBuildOutput(eocvSim)
        }
        fileWorkspace.add(fileWorkspBuildOutput)

        val fileWorkspVSCode = JMenu("VS Code")

        val fileWorkspVSCodeOpen = JMenuItem("Open in the current workspace")

        fileWorkspVSCodeOpen.addActionListener {
            VSCodeLauncher.asyncLaunch(eocvSim.workspaceManager.workspaceFile)
        }
        fileWorkspVSCode.add(fileWorkspVSCodeOpen)

        val fileWorkspVSCodeCreate = JMenuItem("Create VS Code workspace")

        fileWorkspVSCodeCreate.addActionListener { visualizer.createVSCodeWorkspace() }
        fileWorkspVSCode.add(fileWorkspVSCodeCreate)

        fileWorkspace.add(fileWorkspVSCode)

        mFileMenu.add(fileWorkspace)

        val fileSaveMat = JMenuItem("Save current image")

        fileSaveMat.addActionListener {
            GuiUtil.saveMatFileChooser(
                visualizer.frame,
                visualizer.viewport.lastVisualizedMat,
                eocvSim
            )
        }
        mFileMenu.add(fileSaveMat)

        mFileMenu.addSeparator()

        val fileRestart = JMenuItem("Restart")

        fileRestart.addActionListener { eocvSim.onMainUpdate.doOnce { eocvSim.restart() } }
        mFileMenu.add(fileRestart)

        add(mFileMenu)

        val editSettings = JMenuItem("Settings")
        editSettings.addActionListener { DialogFactory.createConfigDialog(eocvSim) }

        mEditMenu.add(editSettings)
        add(mEditMenu)

        val helpUsage = JMenuItem("Usage")
        helpUsage.addActionListener {
            Desktop.getDesktop().browse(URI("https://github.com/serivesmejia/EOCV-Sim/blob/master/USAGE.md"))
        }

        helpUsage.isEnabled = Desktop.isDesktopSupported()
        mHelpMenu.add(helpUsage)

        val helpAbout = JMenuItem("About")
        helpAbout.addActionListener { DialogFactory.createAboutDialog(eocvSim) }

        mHelpMenu.add(helpAbout)
        add(mHelpMenu)
    }

}