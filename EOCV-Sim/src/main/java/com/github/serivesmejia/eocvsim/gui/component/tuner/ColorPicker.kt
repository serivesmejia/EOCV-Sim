package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.component.Viewport
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import org.opencv.core.Scalar
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ColorPicker(private val viewport: Viewport) {

    var isPicking = false
        private set

    val onPick = EventHandler("ColorPicker-OnPick")
    val onCancel = EventHandler("ColorPicker-OnCancel")

    var colorRgb = Scalar(0.0, 0.0, 0.0)
        private set

    val clickListener = object: MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if(e.button == MouseEvent.BUTTON1) {
                val packedColor = viewport.image.image.getRGB(e.x, e.y)
                val color = Color(packedColor, true)

                colorRgb = Scalar(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())

                onPick.run()
            } else {
                onCancel.run()
            }

            stopPicking()
        }
    }

    fun startPicking() {
        if(isPicking) return
        isPicking = true

        viewport.image.addMouseListener(clickListener)
    }

    fun stopPicking() {
        if(!isPicking) return
        isPicking = false

        viewport.image.removeMouseListener(clickListener)
    }

}