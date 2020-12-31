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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

public class TunableTextField extends JTextField {

    private final ArrayList<Character> validCharsIfNumber = new ArrayList<>();

    private final TunableField tunableField;
    private final int index;
    private final Border initialBorder;
    private final EOCVSim eocvSim;
    private volatile boolean hasValidText = true;

    public TunableTextField(int index, TunableField tunableField, EOCVSim eocvSim) {

        super();

        this.initialBorder = this.getBorder();

        this.tunableField = tunableField;
        this.index = index;
        this.eocvSim = eocvSim;

        setText(tunableField.getGuiFieldValue(index).toString());

        int plusW = Math.round(getText().length() / 5f) * 10;
        this.setPreferredSize(new Dimension(40 + plusW, getPreferredSize().height));

        if (tunableField.isOnlyNumbers()) {

            //add all valid characters for non decimal numeric fields
            Collections.addAll(validCharsIfNumber, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-');

            //allow dots for decimal numeric fields
            if (tunableField.getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL) {
                validCharsIfNumber.add('.');
            }

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
                    } catch (NumberFormatException ex) {
                        invalidNumber = true;
                    }

                    hasValidText = !invalidNumber || !text.isEmpty();

                    if (hasValidText) {
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
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            public void change() {
                eocvSim.onMainUpdate.doOnce(() -> {
                    if (!hasValidText || !tunableField.isOnlyNumbers() || !getText().trim().equals("")) {
                        try {
                            tunableField.setGuiFieldValue(index, getText());
                        } catch (Exception e) {
                            setRedBorder();
                        }
                    } else {
                        setRedBorder();
                    }
                });
            }

        });

        //unpausing when typing on any tunable text box
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                execute();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                execute();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                execute();
            }

            public void execute() {
                if (eocvSim.pipelineManager.isPaused()) {
                    eocvSim.pipelineManager.requestSetPaused(false);
                }
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
        for (char validC : validCharsIfNumber) {
            if (c == validC) return true;
        }
        return false;
    }

}
