package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;
import com.github.serivesmejia.eocvsim.input.InputSourceManager.SourceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class CreateSource {

    public volatile JDialog chooseSource = null;

    private volatile JFrame parent = null;

    public static volatile boolean alreadyOpened = false;
    EOCVSim eocvSim = null;

    public CreateSource(JFrame parent, EOCVSim eocvSim) {

        chooseSource = new JDialog(parent);
        this.parent = parent;
        new Thread(() -> initChooseSource()).start();

        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(chooseSource);

    }

    private void initChooseSource() {

        alreadyOpened = true;

        chooseSource.setModal(true);

        chooseSource.setTitle("Select source type");
        chooseSource.setSize(300, 150);

        JPanel contentsPane = new JPanel(new GridLayout(2, 1));

        JPanel dropDownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        SourceType[] sourceTypes = SourceType.values();
        String[] sourceTypesStr = new String[sourceTypes.length-1];

        for(int i = 0 ; i < sourceTypes.length-1 ; i++) {
            sourceTypesStr[i] = sourceTypes[i].toString();
        }

        JComboBox dropDown = new JComboBox(sourceTypesStr);
        dropDownPanel.add(dropDown);
        contentsPane.add(dropDownPanel);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton nextButton = new JButton("Next");

        buttonsPanel.add(nextButton);

        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(cancelButton);

        contentsPane.add(buttonsPanel);

        contentsPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        chooseSource.getContentPane().add(contentsPane, BorderLayout.CENTER);

        cancelButton.addActionListener(e -> close());

        nextButton.addActionListener(e -> {
            close();
            new Thread(() -> {
                switch ((String) Objects.requireNonNull(dropDown.getSelectedItem())) {
                    case "IMAGE":
                        new CreateImageSource(parent, eocvSim);
                        break;
                    case "CAMERA":
                        new CreateCameraSource(parent, eocvSim);
                        break;
                }
            }).start();
        });

        chooseSource.setResizable(false);
        chooseSource.setLocationRelativeTo(null);
        chooseSource.setVisible(true);
        
    }

    public void close() {
        alreadyOpened = false;
        chooseSource.setVisible(false);
        chooseSource.dispose();
    }

}
