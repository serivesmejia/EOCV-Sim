package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;

public class TunableFieldPanel extends JPanel {

    public final TunableField tunableField;

    public JTextArea[] fields;

    public TunableFieldPanel(TunableField tunableField) {
        super();
        this.tunableField = tunableField;
        init();
    }

    private void init() {

        setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

        JLabel fieldNameLabel = new JLabel();
        fieldNameLabel.setText(tunableField.getFieldName());

        add(fieldNameLabel);

        fields = new JTextArea[tunableField.getGuiFieldAmount()];

        for(int i = 0 ; i < fields.length ; i++) {
            fields[i] = new JTextArea(tunableField.getGuiFieldValue(i).toString());
            add(fields[i]);
        }

    }

}