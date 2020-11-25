package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;

import javax.swing.*;

public class Configuration {

    JDialog configuration;

    private EOCVSim eocvSim;

    public Configuration(JFrame parent, EOCVSim eocvSim) {

        configuration = new JDialog(parent);
        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(configuration);

        initConfiguration();

    }

    public void initConfiguration() {

        configuration.setModal(true);

        configuration.setTitle("Settings");
        configuration.setSize(350, 230);

        configuration.setResizable(false);
        configuration.setLocationRelativeTo(null);
        configuration.setVisible(true);

    }

    public void close() {
        configuration.setVisible(false);
        configuration.dispose();
    }

}