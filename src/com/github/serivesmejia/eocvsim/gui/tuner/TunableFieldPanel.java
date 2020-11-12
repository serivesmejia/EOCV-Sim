package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;

public class TunableFieldPanel extends JPanel {

    public final TunableField tunableField;

    public JTextField[] fields;

    public TunableFieldPanel(TunableField tunableField) {
        super();
        this.tunableField = tunableField;
        init();
    }

    private void init() {

        setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        JLabel fieldNameLabel = new JLabel();
        fieldNameLabel.setText(tunableField.getFieldName());

        add(fieldNameLabel);

        fields = new JTextField[tunableField.getGuiFieldAmount()];

        for(int i = 0 ; i < fields.length ; i++) {
            fields[i] = new JTextField(tunableField.getGuiFieldValue(i).toString());
            add(fields[i]);
        }

    }

}