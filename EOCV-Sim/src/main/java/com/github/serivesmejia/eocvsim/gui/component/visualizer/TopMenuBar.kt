package com.github.serivesmejia.eocvsim.gui.component.visualizer

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.gui.DialogFactory
import com.github.serivesmejia.eocvsim.gui.Visualizer
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil
import com.github.serivesmejia.eocvsim.input.SourceType
import java.awt.event.ActionEvent
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class TopMenuBar(visualizer: Visualizer, eocvSim: EOCVSim) : JMenuBar() {

    @JvmField val mFileMenu = JMenu("File")
    @JvmField val mEditMenu = JMenu("Edit")
    @JvmField val mHelpMenu = JMenu("Help")

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

        val fileSaveMatItem = JMenuItem("Save Mat to disk")

        fileSaveMatItem.addActionListener {
            GuiUtil.saveMatFileChooser(
                visualizer.frame,
                visualizer.viewport.lastVisualizedMat,
                eocvSim
            )
        }

        mFileMenu.add(fileSaveMatItem)

        mFileMenu.addSeparator()

        val fileRestart = JMenuItem("Restart")

        fileRestart.addActionListener { eocvSim.onMainUpdate.doOnce(Runnable { eocvSim.restart() }) }

        mFileMenu.add(fileRestart)
        add(mFileMenu)

        val editSettings = JMenuItem("Settings")
        editSettings.addActionListener { DialogFactory.createConfigDialog(eocvSim) }

        mEditMenu.add(editSettings)
        add(mEditMenu)

        val helpAbout = JMenuItem("About")
        helpAbout.addActionListener { DialogFactory.createAboutDialog(eocvSim) }

        mHelpMenu.add(helpAbout)
        add(mHelpMenu)
    }

}