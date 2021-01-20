package com.github.serivesmejia.eocvsim.gui.util.extension

import javax.swing.JTextField
import javax.swing.text.AbstractDocument
import javax.swing.text.DocumentFilter

object SwingExt {

    val JTextField.abstractDocument: AbstractDocument
        get() {
            return (document as AbstractDocument)
        }

    var JTextField.documentFilter: DocumentFilter
        get() {
            return abstractDocument.documentFilter
        }
        set(value) {
            abstractDocument.documentFilter = value
        }

}