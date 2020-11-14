package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;

public class TunableFieldPanel extends JPanel {

    public final TunableField tunableField;
    public JTextField[] fields;

    private boolean isOnlyNumbers = false;

    private EOCVSim eocvSim;

    public TunableFieldPanel(TunableField tunableField, EOCVSim eocvSim) {

        super();

        this.tunableField = tunableField;
        this.eocvSim = eocvSim;

        tunableField.setTunableFieldPanel(this);

        init();

    }

    private void init() {

        setOnlyNumbers(tunableField.isOnlyNumbers());

        //nice look
        setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        JLabel fieldNameLabel = new JLabel();
        fieldNameLabel.setText(tunableField.getFieldName());

        add(fieldNameLabel);

        fields = new JTextField[tunableField.getGuiFieldAmount()];

        for(int i = 0 ; i < fields.length ; i++) {

            TunableTextField field = new TunableTextField(i, tunableField, eocvSim);

            field.setEditable(true);

            add(field);

            fields[i] = field;

        }

    }

    public void setFieldValue(int index, Object value) {
        fields[index].setText(value.toString());
    }

    public void setOnlyNumbers(boolean isOnlyNumbers) {
        this.isOnlyNumbers = isOnlyNumbers;
    }

}