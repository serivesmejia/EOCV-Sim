package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;

import javax.swing.*;
import java.awt.*;

public class FileAlreadyExists {

    public volatile JDialog fileAlreadyExists = null;
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

        fileAlreadyExists.setTitle("File Already Exists");
        fileAlreadyExists.setSize(300, 150);

        JLabel alreadyExistsLabel = new JLabel("Specified file already exists in the selected directory");

        fileAlreadyExists.add(alreadyExistsLabel);

        JPanel replaceCancelPanel = new JPanel(new FlowLayout());

        JButton replaceButton = new JButton("Replace");
        replaceCancelPanel.add(replaceButton);

        replaceButton.addActionListener((e) ->
            userChoice = UserChoice.REPLACE
        );

        JButton cancelButton = new JButton("Cancel");
        replaceCancelPanel.add(cancelButton);

        replaceButton.addActionListener((e) ->
                userChoice = UserChoice.CANCEL
        );

        fileAlreadyExists.add(replaceCancelPanel);

        fileAlreadyExists.setResizable(false);
        fileAlreadyExists.setLocationRelativeTo(null);
        fileAlreadyExists.setVisible(true);

        while(userChoice == UserChoice.NA);

        return userChoice;

    }

}
