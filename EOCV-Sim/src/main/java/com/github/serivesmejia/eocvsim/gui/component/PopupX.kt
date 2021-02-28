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

class PopupX(windowAncestor: Window,
             private val panel: JPanel,
             private val x: Int,
             private val y: Int,
             var closeOnFocusLost: Boolean = true) : Popup(), WindowFocusListener {

    val window = JWindow(windowAncestor)

    @JvmField val onShow = EventHandler("PopupX-OnShow")
    @JvmField val onHide = EventHandler("PopupX-OnHide")

    init {
        window.isFocusable = true
        window.setLocation(x, y)
        window.contentPane = panel

        panel.border = JPopupMenu().border

        window.size = panel.preferredSize

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
        window.addWindowFocusListener(this)
        window.isVisible = true

        //fixes position since our panel dimensions
        //aren't known until it's set visible (above)
        setLocation(x, y - panel.height)

        onShow.run()
    }

    override fun hide() {
        window.removeWindowFocusListener(this)
        window.isVisible = false
        onHide.run()
    }

    override fun windowGainedFocus(e: WindowEvent?) {}

    override fun windowLostFocus(e: WindowEvent?) {
        if(closeOnFocusLost) {
            hide()
        }
    }

    fun setLocation(width: Int, height: Int) = window.setLocation(width, height)

}