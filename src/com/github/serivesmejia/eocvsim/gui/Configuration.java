package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class Configuration {

    JDialog configuration;

    public JComboBox<String> themeComboBox = new JComboBox<>();

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

        //theme selection
        JPanel themePanel = new JPanel(new FlowLayout());

        JLabel themeLabel = new JLabel("Theme: ");
        themeLabel.setHorizontalAlignment(JLabel.CENTER);

        //add all themes to combo box
        for(Theme theme : Theme.values()) {
            themeComboBox.addItem(theme.toString().replace("_", " "));
        }

        //select current theme by index
        themeComboBox.setSelectedIndex(eocvSim.configManager.getConfig().simTheme.ordinal());

        themePanel.add(themeLabel);
        themePanel.add(themeComboBox);

        configuration.add(themePanel);

        configuration.setResizable(false);
        configuration.setLocationRelativeTo(null);
        configuration.setVisible(true);

    }

    public void close() {
        configuration.setVisible(false);
        configuration.dispose();
    }

}