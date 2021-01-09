package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.dialog.*;
import com.github.serivesmejia.eocvsim.input.SourceType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class DialogFactory {

    private final EOCVSim eocvSim;

    public DialogFactory(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public static FileChooser createFileChooser(Component parent, FileChooser.Mode mode, FileFilter... filters) {
        FileChooser fileChooser = new FileChooser(parent, mode, filters);
        invokeLater(fileChooser::init);
        return fileChooser;
    }

    public static FileChooser createFileChooser(Component parent, FileFilter... filters) {
        return createFileChooser(parent, null, filters);
    }

    public static FileChooser createFileChooser(Component parent, FileChooser.Mode mode) {
        return createFileChooser(parent, mode, new FileFilter[0]);
    }

    public static FileChooser createFileChooser(Component parent) {
        return createFileChooser(parent, null, new FileFilter[0]);
    }

    public void createSourceDialog(SourceType type) {
        invokeLater(() -> {
            switch (type) {
                case IMAGE:
                    new CreateImageSource(eocvSim.visualizer.frame, eocvSim);
                    break;
                case CAMERA:
                    new CreateCameraSource(eocvSim.visualizer.frame, eocvSim);
                    break;
                case VIDEO:
                    new CreateVideoSource(eocvSim.visualizer.frame, eocvSim);
            }
        });
    }

    public void createSourceDialog() {
        invokeLater(() -> new CreateSource(eocvSim.visualizer.frame, eocvSim));
    }

    public void createConfigDialog() {
        invokeLater(() -> new Configuration(eocvSim.visualizer.frame, eocvSim));
    }

    public void createAboutDialog() {
        invokeLater(() -> new About(eocvSim.visualizer.frame, eocvSim));
    }

    public FileAlreadyExists.UserChoice createFileAlreadyExistsDialog() {
        return new FileAlreadyExists(eocvSim.visualizer.frame, eocvSim).run();
    }

    private static void invokeLater(Runnable runn) {
        SwingUtilities.invokeLater(runn);
    }

    public static class FileChooser {

        private final JFileChooser chooser;
        private final Component parent;

        private final Mode mode;

        private final ArrayList<FileChooserCloseListener> closeListeners = new ArrayList<>();

        public FileChooser(Component parent, Mode mode, FileFilter... filters) {

            if (mode == null) mode = Mode.FILE_SELECT;

            chooser = new JFileChooser();

            this.parent = parent;
            this.mode = mode;

            if (mode == Mode.DIRECTORY_SELECT) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // disable the "All files" option.
                chooser.setAcceptAllFileFilterUsed(false);
            }

            if (filters != null) {
                for (FileFilter filter : filters) {
                    chooser.addChoosableFileFilter(filter);
                }
            }

        }

        protected void init() {

            int returnVal;

            if (mode == Mode.SAVE_FILE_SELECT) {
                returnVal = chooser.showSaveDialog(parent);
            } else {
                returnVal = chooser.showOpenDialog(parent);
            }

            executeCloseListeners(returnVal, chooser.getSelectedFile(), chooser.getFileFilter());

        }

        public void addCloseListener(FileChooserCloseListener listener) {
            this.closeListeners.add(listener);
        }

        private void executeCloseListeners(int OPTION, File selectedFile, FileFilter selectedFileFilter) {
            for (FileChooserCloseListener listener : closeListeners) {
                listener.onClose(OPTION, selectedFile, selectedFileFilter);
            }
        }

        public void close() {
            chooser.setVisible(false);
            executeCloseListeners(JFileChooser.CANCEL_OPTION, null, null);
        }

        public enum Mode {FILE_SELECT, DIRECTORY_SELECT, SAVE_FILE_SELECT}

        public interface FileChooserCloseListener {
            void onClose(int OPTION, File selectedFile, FileFilter selectedFileFilter);
        }

    }

}
