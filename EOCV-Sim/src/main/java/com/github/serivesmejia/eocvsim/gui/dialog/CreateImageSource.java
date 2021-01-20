package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.DialogFactory;
import com.github.serivesmejia.eocvsim.gui.component.input.SizeFields;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.input.source.ImageSource;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import com.github.serivesmejia.eocvsim.util.FileFilters;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.StrUtil;
import org.opencv.core.Size;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class CreateImageSource {

    public JDialog createImageSource = null;

    public JTextField nameTextField = null;

    public SizeFields sizeFieldsInput = null;

    public JTextField imgDirTextField = null;

    public JButton createButton = null;
    public boolean selectedValidImage = false;
    private EOCVSim eocvSim = null;

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

        sizeFieldsInput = new SizeFields();
        sizeFieldsInput.onChange.doPersistent(this::updateCreateBtt);

        contentsPanel.add(sizeFieldsInput);
        contentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        //Name part

        JPanel namePanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Source name: ");
        nameLabel.setHorizontalAlignment(JLabel.LEFT);

        nameTextField = new JTextField("ImageSource-" + (eocvSim.inputSourceManager.sources.size() + 1), 15);

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

        selectDirButton.addActionListener(e -> {
            DialogFactory.createFileChooser(createImageSource, FileFilters.imagesFilter).addCloseListener((returnVal, selectedFile, selectedFileFilter) -> {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    imageFileSelected(selectedFile);
                }
            });
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

        createButton.addActionListener(e -> {
            createSource(nameTextField.getText(), imgDirTextField.getText(), sizeFieldsInput.getCurrentSize());
            close();
        });

        cancelButton.addActionListener(e -> close());

        createImageSource.setResizable(false);
        createImageSource.setLocationRelativeTo(null);
        createImageSource.setVisible(true);

    }

    public void imageFileSelected(File f) {

        String fileAbsPath = f.getAbsolutePath();

        if (CvUtil.checkImageValid(fileAbsPath)) {
            imgDirTextField.setText(fileAbsPath);

            String fileName = StrUtil.getFileBaseName(f.getName());
            if(!fileName.trim().equals("") && !eocvSim.inputSourceManager.isNameOnUse(fileName)) {
                nameTextField.setText(fileName);
            }

            Size size = CvUtil.scaleToFit(CvUtil.getImageSize(fileAbsPath), EOCVSim.DEFAULT_EOCV_SIZE);

            sizeFieldsInput.getWidthTextField().setText(String.valueOf(size.width));
            sizeFieldsInput.getHeightTextField().setText(String.valueOf(size.height));

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
        eocvSim.onMainUpdate.doOnce(() -> {
            eocvSim.inputSourceManager.addInputSource(sourceName, new ImageSource(imgPath, size));
            eocvSim.visualizer.updateSourcesList();
        });
    }

    public void updateCreateBtt() {
        createButton.setEnabled(!nameTextField.getText().trim().equals("")
                && sizeFieldsInput.getValid()
                && selectedValidImage
                && !eocvSim.inputSourceManager.isNameOnUse(nameTextField.getText()));
    }

}
