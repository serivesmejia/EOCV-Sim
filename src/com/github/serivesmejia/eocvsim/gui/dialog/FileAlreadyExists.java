package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;

import javax.swing.*;
import java.awt.*;

public class FileAlreadyExists {

    public volatile JDialog fileAlreadyExists = null;
    public volatile JPanel contentsPanel = new JPanel();

    public static volatile boolean alreadyOpened = false;
    EOCVSim eocvSim = null;

    public volatile UserChoice userChoice;

    public enum UserChoice { NA, REPLACE, CANCEL }

    public FileAlreadyExists(JFrame parent, EOCVSim eocvSim) {

        fileAlreadyExists = new JDialog(parent);

        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(fileAlreadyExists);

    }

    public UserChoice run() {

        fileAlreadyExists.setModal(true);

        fileAlreadyExists.setTitle("Warning");
        fileAlreadyExists.setSize(300, 120);

        JPanel alreadyExistsPanel = new JPanel(new FlowLayout());

        JLabel alreadyExistsLabel = new JLabel("File already exists in the selected directory");
        alreadyExistsPanel.add(alreadyExistsLabel);

        contentsPanel.add(alreadyExistsPanel);

        JPanel replaceCancelPanel = new JPanel(new FlowLayout());

        JButton replaceButton = new JButton("Replace");
        replaceCancelPanel.add(replaceButton);

        replaceButton.addActionListener((e) -> {
            userChoice = UserChoice.REPLACE;
            fileAlreadyExists.setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        replaceCancelPanel.add(cancelButton);

        cancelButton.addActionListener((e) -> {
            userChoice = UserChoice.CANCEL;
            fileAlreadyExists.setVisible(false);
        });

        contentsPanel.add(replaceCancelPanel);

        fileAlreadyExists.add(contentsPanel);

        fileAlreadyExists.setResizable(false);
        fileAlreadyExists.setLocationRelativeTo(null);
        fileAlreadyExists.setVisible(true);

        while(userChoice == UserChoice.NA);

        return userChoice;

    }

}
