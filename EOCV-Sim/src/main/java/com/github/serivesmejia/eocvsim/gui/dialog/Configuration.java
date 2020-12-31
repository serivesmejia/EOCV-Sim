package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.config.Config;
import com.github.serivesmejia.eocvsim.gui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class Configuration {

    private final EOCVSim eocvSim;
    public JPanel contents = new JPanel(new GridLayout(4, 1));
    public JComboBox<String> themeComboBox = new JComboBox<>();

    public JButton acceptButton = new JButton("Accept");

    public JCheckBox storeZoomCheckBox = new JCheckBox();
    public JCheckBox pauseOnImageCheckBox = new JCheckBox();
    JDialog configuration;

    public Configuration(JFrame parent, EOCVSim eocvSim) {

        configuration = new JDialog(parent);
        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(configuration);

        initConfiguration();

    }

    private void initConfiguration() {

        Config config = eocvSim.configManager.getConfig();

        configuration.setModal(true);

        configuration.setTitle("Settings");
        configuration.setSize(350, 230);

        //theme selection
        JPanel themePanel = new JPanel(new FlowLayout());

        JLabel themeLabel = new JLabel("Theme: ");
        themeLabel.setHorizontalAlignment(JLabel.CENTER);

        //add all themes to combo box
        for (Theme theme : Theme.values()) {
            themeComboBox.addItem(theme.toString().replace("_", " "));
        }

        //select current theme by index
        themeComboBox.setSelectedIndex(eocvSim.configManager.getConfig().simTheme.ordinal());

        themePanel.add(themeLabel);
        themePanel.add(themeComboBox);

        contents.add(themePanel);

        //store zoom option
        JPanel storeZoomPanel = new JPanel(new FlowLayout());
        JLabel storeZoomLabel = new JLabel("Store zoom value");

        storeZoomCheckBox.setSelected(config.storeZoom);

        storeZoomPanel.add(storeZoomCheckBox);
        storeZoomPanel.add(storeZoomLabel);

        contents.add(storeZoomPanel);

        //pause on image option
        JPanel pauseOnImagePanel = new JPanel(new FlowLayout());
        JLabel pauseOnImageLabel = new JLabel("Pause with image sources");

        pauseOnImageCheckBox.setSelected(config.pauseOnImages);

        pauseOnImagePanel.add(pauseOnImageCheckBox);
        pauseOnImagePanel.add(pauseOnImageLabel);

        contents.add(pauseOnImagePanel);

        //accept button
        JPanel acceptPanel = new JPanel(new FlowLayout());
        acceptPanel.add(acceptButton);

        acceptButton.addActionListener((e) -> {
            eocvSim.onMainUpdate.doOnce(this::applyChanges);
            close();
        });

        contents.add(acceptPanel);

        contents.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        configuration.add(contents);

        configuration.setResizable(false);
        configuration.setLocationRelativeTo(null);
        configuration.setVisible(true);

    }

    private void applyChanges() {

        Config config = eocvSim.configManager.getConfig();

        Theme userSelectedTheme = Theme.valueOf(themeComboBox.getSelectedItem().toString().replace(" ", "_"));
        Theme beforeTheme = config.simTheme;

        //save user modifications to config
        config.simTheme = userSelectedTheme;
        config.storeZoom = storeZoomCheckBox.isSelected();
        config.pauseOnImages = pauseOnImageCheckBox.isSelected();

        eocvSim.configManager.saveToFile(); //update config file

        if (userSelectedTheme != beforeTheme)
            eocvSim.restart();

    }

    public void close() {
        configuration.setVisible(false);
        configuration.dispose();
    }

}