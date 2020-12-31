package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.input.source.CameraSource;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateCameraSource {

    public JDialog createCameraSource = null;

    public JTextField cameraIdField = null;
    public JButton createButton = null;

    public JTextField widthTextField = null;
    public JTextField heightTextField = null;

    public JTextField nameTextField = null;
    public boolean wasCancelled = false;
    private boolean validCameraIdNumber = true;
    private boolean validCameraSizeNumbers = true;
    private final EOCVSim eocvSim;

    public CreateCameraSource(JFrame parent, EOCVSim eocvSim) {

        createCameraSource = new JDialog(parent);
        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(createCameraSource);

        initCreateImageSource();

    }

    public void initCreateImageSource() {

        createCameraSource.setModal(true);

        createCameraSource.setTitle("Create camera source");
        createCameraSource.setSize(350, 230);

        JPanel contentsPanel = new JPanel(new GridLayout(5, 1));

        // Camera id part

        JPanel idPanel = new JPanel(new FlowLayout());

        JLabel idLabel = new JLabel("Camera ID: ");
        idLabel.setHorizontalAlignment(JLabel.CENTER);

        cameraIdField = new JTextField("0", 4);

        idPanel.add(idLabel);
        idPanel.add(cameraIdField);

        contentsPanel.add(idPanel);

        //Name part

        JPanel namePanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Source name: ");

        nameTextField = new JTextField("CameraSource-" + (eocvSim.inputSourceManager.sources.size() + 1), 15);

        namePanel.add(nameLabel);
        namePanel.add(nameTextField);

        contentsPanel.add(namePanel);

        // Size part

        JPanel sizePanel = new JPanel(new FlowLayout());

        JLabel sizeLabel = new JLabel("Size: ");
        sizeLabel.setHorizontalAlignment(JLabel.LEFT);

        widthTextField = new JTextField(String.valueOf(EOCVSim.DEFAULT_EOCV_WIDTH), 4);

        sizePanel.add(sizeLabel);
        sizePanel.add(widthTextField);

        JLabel xSizeLabel = new JLabel(" x ");
        xSizeLabel.setHorizontalAlignment(JLabel.CENTER);

        heightTextField = new JTextField(String.valueOf(EOCVSim.DEFAULT_EOCV_HEIGHT), 4);

        sizePanel.add(xSizeLabel);
        sizePanel.add(heightTextField);

        contentsPanel.add(sizePanel);

        contentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        sizePanel.add(xSizeLabel);
        sizePanel.add(heightTextField);

        contentsPanel.add(sizePanel);

        contentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

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
        GuiUtil.jTextFieldOnlyNumbers(widthTextField, 0, EOCVSim.DEFAULT_EOCV_WIDTH);
        GuiUtil.jTextFieldOnlyNumbers(heightTextField, 0, EOCVSim.DEFAULT_EOCV_HEIGHT);

        DocumentListener validSizeNumberListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed(e);
            }

            public void removeUpdate(DocumentEvent e) {
                changed(e);
            }

            public void insertUpdate(DocumentEvent e) {
                changed(e);
            }

            public void changed(DocumentEvent e) {
                try {
                    Integer.parseInt(widthTextField.getText());
                    Integer.parseInt(heightTextField.getText());
                    validCameraSizeNumbers = true;
                } catch (Throwable ex) {
                    validCameraSizeNumbers = false;
                }
                updateCreateBtt();
            }
        };

        widthTextField.getDocument().addDocumentListener(validSizeNumberListener);
        heightTextField.getDocument().addDocumentListener(validSizeNumberListener);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int camId = Integer.parseInt(cameraIdField.getText());
                int width = Integer.parseInt(widthTextField.getText());
                int height = Integer.parseInt(heightTextField.getText());

                statusLabel.setText("Trying to open camera, please wait...");
                cameraIdField.setEditable(false);
                createButton.setEnabled(false);

                eocvSim.onMainUpdate.doOnce(() -> {
                    if (testCamera(camId)) {
                        close();
                        if (wasCancelled) return;
                        createSource(nameTextField.getText(), camId, new Size(width, height));
                        eocvSim.visualizer.updateSourcesList();
                    } else {
                        cameraIdField.setEditable(true);
                        createButton.setEnabled(true);
                        statusLabel.setText("Failed to open camera, try with another index.");
                    }
                });

            }
        });

        cameraIdField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                try {
                    Integer.parseInt(cameraIdField.getText());
                    validCameraIdNumber = true;
                } catch (Throwable ex) {
                    validCameraIdNumber = false;
                }
                updateCreateBtt();
            }
        });

        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                updateCreateBtt();
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

        VideoCapture camera = new VideoCapture();
        camera.open(camIndex);

        boolean wasOpened = camera.isOpened();

        camera.release();

        return wasOpened;

    }

    public void createSource(String sourceName, int index, Size size) {
        eocvSim.onMainUpdate.doOnce(() -> {
            eocvSim.inputSourceManager.addInputSource(sourceName, new CameraSource(index, size));
            eocvSim.visualizer.updateSourcesList();
        });
    }

    public void updateCreateBtt() {
        createButton.setEnabled(!nameTextField.getText().trim().equals("")
                && validCameraIdNumber
                && validCameraSizeNumbers);
    }

}