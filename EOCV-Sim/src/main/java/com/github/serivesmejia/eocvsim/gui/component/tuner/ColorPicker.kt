package com.github.serivesmejia.eocvsim.gui.component.tuner

import com.github.serivesmejia.eocvsim.gui.component.ImageX
import com.github.serivesmejia.eocvsim.gui.component.Viewport
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import org.opencv.core.Scalar
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ColorPicker(private val imageX: ImageX) {

    var isPicking = false
        private set

    var hasPicked = false
        private set

    val onPick = EventHandler("ColorPicker-OnPick")
    val onCancel = EventHandler("ColorPicker-OnCancel")

    var colorRgb = Scalar(0.0, 0.0, 0.0)
        private set

    val clickListener = object: MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            //if clicked with primary button...
            if(e.button == MouseEvent.BUTTON1) {
                //get the "packed" (in a single int value) color from the image at mouse position's pixel
                val packedColor = imageX.image.getRGB(e.x, e.y)
                //parse the "packed" color into four separate channels
                val color = Color(packedColor, true)

                //wrap Java's color to OpenCV's Scalar since we're EOCV-Sim not JavaCv-Sim right?
                colorRgb = Scalar(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())

                hasPicked = true
                onPick.run() //run all oick listeners
            } else {
                onCancel.run()
            }

            stopPicking()
        }
    }

    fun startPicking() {
        if(isPicking) return
        isPicking = true
        hasPicked = false

        imageX.addMouseListener(clickListener)
    }

    fun stopPicking() {
        if(!isPicking) return
        isPicking = false

        if(!hasPicked) {
            onPick.removeAllListeners()
            onCancel.run()
        }

        imageX.removeMouseListener(clickListener)
    }

}