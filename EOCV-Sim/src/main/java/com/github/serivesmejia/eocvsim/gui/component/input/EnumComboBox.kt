package com.github.serivesmejia.eocvsim.gui.component.input

import com.github.serivesmejia.eocvsim.util.event.EventHandler
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class EnumComboBox<T : Enum<T>>(descriptiveText: String = "Select a value:",
                                private val clazz: Class<T>,
                                values: Array<T>) : JPanel() {

    val descriptiveLabel = JLabel(descriptiveText)
    val comboBox = JComboBox<String>()

    var selectedEnum: T?
        set(value) {
            comboBox.selectedItem = value?.name
        }
        get() {
            comboBox.selectedItem?.let {
                return java.lang.Enum.valueOf(clazz, comboBox.selectedItem.toString())
            }
            return null
        }

    val onSelect = EventHandler("EnumComboBox-OnSelect")

    init {
        descriptiveLabel.horizontalAlignment = JLabel.LEFT
        add(descriptiveLabel)

        for(value in values) {
            comboBox.addItem(value.name)
        }
        add(comboBox)

        comboBox.addActionListener { onSelect.run() }
    }

}