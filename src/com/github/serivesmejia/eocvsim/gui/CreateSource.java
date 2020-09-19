package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.input.InputSourceManager;

import javax.swing.*;
import java.awt.*;

public class CreateSource {

    public JDialog chooseSource = null;

    public JDialog createCamSource = null;

    public CreateSource(JFrame parent) {

        chooseSource = new JDialog(parent);
        initChooseSource();

    }

    public void initChooseSource() {
        
        chooseSource.setModal(true);

        chooseSource.setTitle("Select source type");
        chooseSource.setSize(300, 150);
        chooseSource.setLayout(new GridLayout(2, 1));

        JPanel dropDownPanel = new JPanel(new FlowLayout());

        InputSourceManager.SourceType[] sourceTypes = InputSourceManager.SourceType.values();
        String[] sourceTypesStr = new String[sourceTypes.length-1];

        for(int i = 0 ; i < sourceTypes.length-1 ; i++) {
            sourceTypesStr[i] = sourceTypes[i].toString();
        }

        JComboBox dropDown = new JComboBox(sourceTypesStr);
        dropDownPanel.add(dropDown);
        chooseSource.getContentPane().add(dropDownPanel);

        JPanel nextButtonPanel = new JPanel(new FlowLayout());
        JButton nextButton = new JButton("Next");

        nextButtonPanel.add(nextButton);

        chooseSource.getContentPane().add(nextButtonPanel);

        chooseSource.setLocationRelativeTo(null);
        chooseSource.setVisible(true);
        
    }

}
