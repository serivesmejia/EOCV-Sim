package com.github.serivesmejia.eocvsim.gui.component.input

import javax.swing.JComboBox

class EnumComboBox<T : Enum<T>>(private val clazz: Class<T>,
                                private val values: Array<T>) : JComboBox<String>() {

    var selectedEnum: T?
        set(value) {
            selectedItem = value?.name
        }
        get() {
            selectedItem?.let {
                return java.lang.Enum.valueOf(clazz, selectedItem.toString())
            }
            return null
        }

    init {
        for(value in values) {
            addItem(value.name)
        }
    }

}