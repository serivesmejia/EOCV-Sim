package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.input.CameraSource;
import com.github.serivesmejia.eocvsim.input.ImageSource;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class CreateCameraSource {

    public JDialog createCameraSource = null;

    public JTextField cameraIdField = null;

    public JButton createButton = null;

    public boolean wasCancelled = false;

    private EOCVSim eocvSim = null;

    public CreateCameraSource(JFrame parent, EOCVSim eocvSim) {

        createCameraSource = new JDialog(parent);
        this.eocvSim = eocvSim;

        initCreateImageSource();

    }

    public void initCreateImageSource() {

        createCameraSource.setModal(true);

        createCameraSource.setTitle("Create camera source");
        createCameraSource.setSize(350, 200);

        JPanel contentsPanel = new JPanel(new GridLayout(4, 1));

        // Camera id part

        JPanel idPanel = new JPanel(new FlowLayout());

        JLabel idLabel = new JLabel("Camera ID: ");
        idLabel.setHorizontalAlignment(JLabel.CENTER);

        cameraIdField = new JTextField("0",4);

        idPanel.add(idLabel);
        idPanel.add(cameraIdField);

        contentsPanel.add(idPanel);

        //Name part

        JPanel namePanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Source name: ");

        JTextField nameTextField = new JTextField("CameraSource-" + (eocvSim.inputSourceManager.sources.size() + 1),15);

        namePanel.add(nameLabel);
        namePanel.add(nameTextField);

        contentsPanel.add(namePanel);

        // Status label part

        JLabel statusLabel = new JLabel("Click \"create\" to test camera.");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        contentsPanel.add(statusLabel);

        // Bottom buttons

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        createButton = new JButton("Create");

        buttonsPanel.add(createButton);

        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(cancelButton);

        contentsPanel.add(buttonsPanel);

        //Add contents

        contentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        createCameraSource.getContentPane().add(contentsPanel, BorderLayout.CENTER);

        // Additional stuff & events

        GuiUtil.jTextFieldOnlyNumbers(cameraIdField, -100, 0);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int camId = Integer.parseInt(cameraIdField.getText());

                statusLabel.setText("Trying to open camera, please wait...");
                cameraIdField.setEditable(false);
                createButton.setEnabled(false);

                eocvSim.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(testCamera(camId)) {
                            close();
                            if(wasCancelled) return;
                            eocvSim.inputSourceManager.addInputSource(nameTextField.getText(), new CameraSource(camId));
                            eocvSim.visualizer.updateSourcesList();
                        } else {
                            cameraIdField.setEditable(true);
                            createButton.setEnabled(true);
                            statusLabel.setText("Failed to open camera, try with another index.");
                        }
                    }
                });

            }
        });


        cameraIdField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void insertUpdate(DocumentEvent e) { changed(); }
            public void changed() {
                try {
                    Integer.parseInt(cameraIdField.getText());
                    createButton.setEnabled(true);
                } catch(Throwable ex) {
                    createButton.setEnabled(false);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wasCancelled = true;
                close();
            }
        });

        createCameraSource.setResizable(false);
        createCameraSource.setLocationRelativeTo(null);
        createCameraSource.setVisible(true);

    }

    public void close() {
        createCameraSource.setVisible(false);
        createCameraSource.dispose();
    }

    public boolean testCamera(int camIndex) {

        VideoCapture camera = new VideoCapture(camIndex);
        boolean wasOpened = camera.isOpened();

        camera.release();

        return wasOpened;

    }

    public void createSource(String sourceName, String imgPath, Size size) {
        eocvSim.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                eocvSim.inputSourceManager.addInputSource(sourceName, new ImageSource(imgPath, size));
                eocvSim.visualizer.updateSourcesList();
            }
        });
    }

}
