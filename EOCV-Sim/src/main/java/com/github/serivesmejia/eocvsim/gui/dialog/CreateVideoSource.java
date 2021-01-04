package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.DialogFactory;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.input.source.ImageSource;
import com.github.serivesmejia.eocvsim.input.source.VideoSource;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import org.opencv.core.Size;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class CreateVideoSource {

    public JDialog createVideoSource = null;

    public JTextField nameTextField = null;

    public JTextField vidDirTextField = null;
    public JTextField widthTextField = null;
    public JTextField heightTextField = null;

    public JButton createButton = null;
    public boolean selectedValidVideo = false;
    private boolean validVideoSizeNumbers = true;

    private EOCVSim eocvSim = null;

    public CreateVideoSource(JFrame parent, EOCVSim eocvSim) {

        createVideoSource = new JDialog(parent);
        this.eocvSim = eocvSim;

        eocvSim.visualizer.childDialogs.add(createVideoSource);

        initCreateImageSource();

    }

    public void initCreateImageSource() {

        createVideoSource.setModal(true);

        createVideoSource.setTitle("Create video source");
        createVideoSource.setSize(370, 200);

        JPanel contentsPanel = new JPanel(new GridLayout(4, 1));

        JPanel vidDirPanel = new JPanel(new FlowLayout());

        vidDirTextField = new JTextField(18);
        vidDirTextField.setEditable(false);
        JButton selectDirButton = new JButton("Select file...");

        vidDirPanel.add(vidDirTextField);
        vidDirPanel.add(selectDirButton);

        contentsPanel.add(vidDirPanel);

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

        nameTextField = new JTextField("VideoSource-" + (eocvSim.inputSourceManager.sources.size() + 1), 15);

        namePanel.add(nameLabel);
        namePanel.add(nameTextField);

        contentsPanel.add(namePanel);

        // Bottom buttons

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        createButton = new JButton("Create");
        createButton.setEnabled(selectedValidVideo);

        buttonsPanel.add(createButton);

        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(cancelButton);

        contentsPanel.add(buttonsPanel);

        //Add contents

        createVideoSource.getContentPane().add(contentsPanel, BorderLayout.CENTER);

        // Additional stuff & events

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
                    validVideoSizeNumbers = true;
                } catch (Throwable ex) {
                    validVideoSizeNumbers = false;
                }
                updateCreateBtt();
            }
        };

        widthTextField.getDocument().addDocumentListener(validSizeNumberListener);
        heightTextField.getDocument().addDocumentListener(validSizeNumberListener);

        selectDirButton.addActionListener(e -> {

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Video Media",
                    "avi", "mkv", "mov", "mp4");

            DialogFactory.createFileChooser(createVideoSource, filter).addCloseListener((returnVal, selectedFile, selectedFileFilter) -> {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    videoFileSelected(selectedFile);
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
            int width = Integer.parseInt(widthTextField.getText());
            int height = Integer.parseInt(heightTextField.getText());
            createSource(nameTextField.getText(), vidDirTextField.getText(), new Size(width, height));
            close();
        });


        // Status label part

        cancelButton.addActionListener(e -> close());

        createVideoSource.setResizable(false);
        createVideoSource.setLocationRelativeTo(null);
        createVideoSource.setVisible(true);

    }

    public void videoFileSelected(File f) {

        String fileAbsPath = f.getAbsolutePath();

        if (CvUtil.checkVideoValid(fileAbsPath)) {
            vidDirTextField.setText(fileAbsPath);
            selectedValidVideo = true;
        } else {
            vidDirTextField.setText("Unable to load selected file.");
            selectedValidVideo = false;
        }

        updateCreateBtt();

    }

    public void close() {
        createVideoSource.setVisible(false);
        createVideoSource.dispose();
    }

    public void createSource(String sourceName, String videoPath, Size size) {
        eocvSim.onMainUpdate.doOnce(() -> {
            eocvSim.inputSourceManager.addInputSource(sourceName, new VideoSource(videoPath, size));
            eocvSim.visualizer.updateSourcesList();
        });
    }

    public void updateCreateBtt() {
        createButton.setEnabled(!nameTextField.getText().trim().equals("")
                && validVideoSizeNumbers
                && selectedValidVideo);
    }

}
