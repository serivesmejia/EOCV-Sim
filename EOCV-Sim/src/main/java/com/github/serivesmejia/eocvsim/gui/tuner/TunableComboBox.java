package com.github.serivesmejia.eocvsim.gui.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;

import javax.swing.*;

public class TunableComboBox extends JComboBox<String> {

    private final TunableField tunableField;
    private final int index;

    private final EOCVSim eocvSim;

    public TunableComboBox(int index, TunableField tunableField, EOCVSim eocvSim) {

        super();

        this.tunableField = tunableField;
        this.index = index;
        this.eocvSim = eocvSim;

        init();

    }

    private void init() {

        for (Object obj : tunableField.getGuiComboBoxValues(index)) {
            this.addItem(obj.toString());
        }

        addActionListener(e -> eocvSim.onMainUpdate.addListener(() -> {

            try {
                tunableField.setGuiComboBoxValue(index, getSelectedItem().toString());
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }

            if (eocvSim.pipelineManager.isPaused()) {
                eocvSim.pipelineManager.requestSetPaused(false);
            }

        }));

    }

}