package com.github.serivesmejia.eocvsim.gui

import com.github.serivesmejia.eocvsim.gui.util.GuiUtil
import java.awt.image.BufferedImage
import java.util.NoSuchElementException
import javax.swing.ImageIcon

object Icons {

    private val bufferedImages = HashMap<String, BufferedImage>()
    private val icons = HashMap<String, ImageIcon>()

    private var colorsInverted = false

    init {
        addImage("ico_img", "/images/icon/ico_img.png")
        addImage("ico_cam", "/images/icon/ico_cam.png")
        addImage("ico_vid", "/images/icon/ico_vid.png")
    }

    fun getImage(name: String): ImageIcon {
        if(!icons.containsKey(name)) {
            throw NoSuchElementException("Image $name is not loaded into memory")
        }
        return icons[name]!!
    }

    fun getImageResized(name: String, width: Int, height: Int) = GuiUtil.scaleImage(getImage(name), width, height)

    fun addImage(name: String, path: String) {
        val buffImg = GuiUtil.loadBufferedImage(path)
        if(colorsInverted) {
            GuiUtil.invertBufferedImageColors(buffImg)
        }

        bufferedImages[name] = buffImg
        icons[name] = ImageIcon(buffImg)
    }

    fun setDark(dark: Boolean) {
        if(dark) {
            if(!colorsInverted) {
                invertAll()
                colorsInverted = true
            }
        } else {
            if(colorsInverted) {
                invertAll()
                colorsInverted = false
            }
        }
    }

    fun invertAll() {
        for((name, img) in bufferedImages) {
            GuiUtil.invertBufferedImageColors(img)
        }
        redefineIcons()
    }

    private fun redefineIcons() {
        for((name, img) in bufferedImages) {
            icons[name] = ImageIcon(img)
        }
    }

}