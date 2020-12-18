package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;

public class TunableFieldPanel extends JPanel {

    public final TunableField tunableField;

    public JTextField[] fields;
    public JComboBox[] comboBoxes;

    private boolean isOnlyNumbers = false;

    private final EOCVSim eocvSim;

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

        for (int i = 0; i < fields.length; i++) {

            TunableTextField field = new TunableTextField(i, tunableField, eocvSim);

            field.setEditable(true);
            add(field);

            fields[i] = field;

        }

        comboBoxes = new JComboBox[tunableField.getGuiComboBoxAmount()];

        for (int i = 0; i < comboBoxes.length; i++) {

            TunableComboBox comboBox = new TunableComboBox(i, tunableField, eocvSim);
            add(comboBox);

            comboBoxes[i] = comboBox;

        }

    }

    public void setFieldValue(int index, Object value) {
        fields[index].setText(value.toString());
    }

    public void setComboBoxSelection(int index, Object selection) {
        comboBoxes[index].setSelectedItem(selection.toString());
    }

    public void setOnlyNumbers(boolean isOnlyNumbers) {
        this.isOnlyNumbers = isOnlyNumbers;
    }

}