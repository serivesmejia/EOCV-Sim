package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class TunableTextField extends JTextField {

    static char[] validCharsIfNumber = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.'};

    private final TunableField tunableField;
    private final int index;

    private EOCVSim eocvSim;

    private final Border initialBorder;

    private volatile boolean hasValidText = false;

    public TunableTextField(int index, TunableField tunableField, EOCVSim eocvSim) {

        super();

        this.initialBorder = this.getBorder();

        this.tunableField = tunableField;
        this.index = index;
        this.eocvSim = eocvSim;

        setText(tunableField.getGuiFieldValue(index).toString());

        if(tunableField.isOnlyNumbers()) {
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

                    text = text.replace(" ", "");

                    for (char c : text.toCharArray()) {
                        if (!isNumberCharacter(c)) return;
                    }

                    boolean invalidNumber = false;

                    try { //check if entered text is valid number
                        Double.valueOf(text);
                    } catch(NumberFormatException ex) {
                        invalidNumber = true;
                    }

                    hasValidText = !invalidNumber || !text.isEmpty();

                    if(hasValidText) {
                        setNormalBorder();
                    } else {
                        setRedBorder();
                    }

                    super.replace(fb, offset, length, text, attrs);

                }
            });

        }

        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) { change(); }
            @Override
            public void removeUpdate(DocumentEvent e) { change(); }
            @Override
            public void changedUpdate(DocumentEvent e) { change(); }

            public void change() {
                eocvSim.runOnMainThread(() -> {
                    if(hasValidText && !TunableTextField.this.getText().isBlank()) {
                        try {
                            tunableField.setGuiFieldValue(index, TunableTextField.this.getText());
                        } catch (Exception e) {
                            setRedBorder();
                        }
                    } else {
                        setRedBorder();
                    }
                });
            }

        });

    }
    public void setNormalBorder() {
        setBorder(initialBorder);
    }

    public void setRedBorder() {
        setBorder(new LineBorder(new Color(255, 79, 79), 2));
    }

    private boolean isNumberCharacter(char c) {
        for(char validC : validCharsIfNumber) {
            if(c == validC) return true;
        }
        return false;
    }

}
