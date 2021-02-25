package com.github.serivesmejia.eocvsim.gui.component

import com.github.serivesmejia.eocvsim.util.event.EventHandler
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

    val onShow = EventHandler("PopupX-OnShow")
    val onHide = EventHandler("PopupX-OnHide")

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
        onShow.run()
    }

    override fun hide() {
        dialog.removeWindowFocusListener(this)
        dialog.isVisible = false
        onHide.run()
    }

    override fun windowGainedFocus(e: WindowEvent?) {}

    override fun windowLostFocus(e: WindowEvent?) = hide()
}