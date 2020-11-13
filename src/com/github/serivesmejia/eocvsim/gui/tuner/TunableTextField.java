package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TunableTextField extends JTextField {

    static char[] validCharsIfNumber = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.'};

    private final TunableField tunableField;
    private final int index;

    public TunableTextField(int index, TunableField tunableField) {

        super();

        this.tunableField = tunableField;
        this.index = index;

        setText(tunableField.getGuiFieldValue(index).toString());

        if(tunableField.isOnlyNumbers()) {

            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

                    text = text.replace(" ", "");

                    for (char c : text.toCharArray()) {
                        if (!isNumberCharacter(c)) return;
                    }

                    super.replace(fb, offset, length, text, attrs);

                }
            });

        }

    }

    private boolean isNumberCharacter(char c) {
        for(char validC : validCharsIfNumber) {
            if(c == validC) return true;
        }
        return false;
    }

}
