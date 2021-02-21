package com.github.serivesmejia.eocvsim.gui.component

import java.awt.Window
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JWindow
import javax.swing.Popup

class PopupX(windowAncestor: Window, panel: JPanel, x: Int, y: Int) : Popup(), WindowFocusListener {

    private val dialog = JWindow(windowAncestor)

    init {
        dialog.isFocusable = true
        dialog.setLocation(x, y)
        dialog.contentPane = panel

        panel.border = JPopupMenu().border

        dialog.size = panel.preferredSize

        windowAncestor.addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if(e?.keyCode == KeyEvent.VK_ESCAPE) {
                    hide()
                    windowAncestor.removeKeyListener(this)
                }
            }
        })
    }

    override fun show() {
        dialog.addWindowFocusListener(this)
        dialog.isVisible = true
    }

    override fun hide() {
        dialog.removeWindowFocusListener(this)
        dialog.isVisible = false
    }

    override fun windowGainedFocus(e: WindowEvent?) {}

    override fun windowLostFocus(e: WindowEvent?) = hide()
}