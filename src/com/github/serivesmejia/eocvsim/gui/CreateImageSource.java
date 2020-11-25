package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.input.source.ImageSource;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import org.opencv.core.Size;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreateImageSource {

    public JDialog createImageSource = null;

    public JTextField nameTextField = null;

    public JTextField imgDirTextField = null;
    public JTextField widthTextField = null;
    public JTextField heightTextField = null;

    public JButton createButton = null;

    private EOCVSim eocvSim = null;

    public boolean selectedValidImage = false;

    private boolean validCameraSizeNumbers = true;

    public CreateImageSource(JFrame parent, EOCVSim eocvSim) {

        createImageSource = new JDialog(parent);
        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(createImageSource);

        initCreateImageSource();

    }

    public void initCreateImageSource() {

        createImageSource.setModal(true);

        createImageSource.setTitle("Create image source");
        createImageSource.setSize(370, 200);

        JPanel contentsPanel = new JPanel(new GridLayout(4, 1));

        JPanel imgDirPanel = new JPanel(new FlowLayout());

        imgDirTextField = new JTextField(18);
        imgDirTextField.setEditable(false);
        JButton selectDirButton = new JButton("Select file...");

        imgDirPanel.add(imgDirTextField);
        imgDirPanel.add(selectDirButton);

        contentsPanel.add(imgDirPanel);

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

        //Name part

        JPanel namePanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Source name: ");
        sizeLabel.setHorizontalAlignment(JLabel.LEFT);

        nameTextField = new JTextField("ImageSource-" + (eocvSim.inputSourceManager.sources.size() + 1),15);

        namePanel.add(nameLabel);
        namePanel.add(nameTextField);

        contentsPanel.add(namePanel);

        // Bottom buttons

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        createButton = new JButton("Create");
        createButton.setEnabled(selectedValidImage);

        buttonsPanel.add(createButton);

        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(cancelButton);

        contentsPanel.add(buttonsPanel);

        //Add contents

        createImageSource.getContentPane().add(contentsPanel, BorderLayout.CENTER);

        // Additional stuff & events

        GuiUtil.jTextFieldOnlyNumbers(widthTextField, 0, EOCVSim.DEFAULT_EOCV_WIDTH);
        GuiUtil.jTextFieldOnlyNumbers(heightTextField, 0, EOCVSim.DEFAULT_EOCV_HEIGHT);

        DocumentListener validSizeNumberListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { changed(e); }
            public void removeUpdate(DocumentEvent e) { changed(e); }
            public void insertUpdate(DocumentEvent e) { changed(e); }
            public void changed(DocumentEvent e) {
                try {
                    Integer.parseInt(widthTextField.getText());
                    Integer.parseInt(heightTextField.getText());
                    validCameraSizeNumbers = true;
                } catch(Throwable ex) {
                    validCameraSizeNumbers = false;
                }
                updateCreateBtt();
            }
        };

        widthTextField.getDocument().addDocumentListener(validSizeNumberListener);
        heightTextField.getDocument().addDocumentListener(validSizeNumberListener);

        selectDirButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Images", "jpg", "jpeg", "jpe", "jp2","bmp", "png", "tiff", "tif");
            chooser.setFileFilter(filter);

            int returnVal = chooser.showOpenDialog(createImageSource);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                imageFileSelected(chooser.getSelectedFile());
            }

        });

        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void insertUpdate(DocumentEvent e) { changed(); }
            public void changed() {
                updateCreateBtt();
            }
        });

        createButton.addActionListener(e -> {
            int width = Integer.parseInt(widthTextField.getText());
            int height = Integer.parseInt(heightTextField.getText());
            createSource(nameTextField.getText(), imgDirTextField.getText(), new Size(width, height));
            close();
        });

        cancelButton.addActionListener(e -> close());

        createImageSource.setResizable(false);
        createImageSource.setLocationRelativeTo(null);
        createImageSource.setVisible(true);

    }

    public void imageFileSelected(File f) {

        String fileAbsPath = f.getAbsolutePath();

        if(CvUtil.checkImageValid(fileAbsPath)) {
            imgDirTextField.setText(fileAbsPath);
            selectedValidImage = true;
        } else {
            imgDirTextField.setText("Unable to load selected file.");
            selectedValidImage = false;
        }

        updateCreateBtt();

    }

    public void close() {
        createImageSource.setVisible(false);
        createImageSource.dispose();
    }

    public void createSource(String sourceName, String imgPath, Size size) {
        eocvSim.runOnMainThread(() -> {
            eocvSim.inputSourceManager.addInputSource(sourceName, new ImageSource(imgPath, size));
            eocvSim.visualizer.updateSourcesList();
        });
    }

    public void updateCreateBtt() {
        createButton.setEnabled(!nameTextField.getText().trim().equals("")
                                && validCameraSizeNumbers
                                && selectedValidImage);
    }

}
