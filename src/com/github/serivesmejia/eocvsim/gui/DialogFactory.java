package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.dialog.Configuration;
import com.github.serivesmejia.eocvsim.gui.dialog.CreateCameraSource;
import com.github.serivesmejia.eocvsim.gui.dialog.CreateImageSource;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;

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

    public Thread createSourceDialog(InputSourceManager.SourceType type) {
        return createStartThread(() -> {
            switch (type) {
                case IMAGE:
                    new CreateImageSource(eocvSim.visualizer.frame, eocvSim);
                    break;
                case CAMERA:
                    new CreateCameraSource(eocvSim.visualizer.frame, eocvSim);
                    break;
            }
        });
    }

    public Thread createConfigDialog() {
        return createStartThread(() -> new Configuration(eocvSim.visualizer.frame, eocvSim));
    }

    public static FileChooser createFileChooser(Component parent, FileChooser.Mode mode, FileFilter... filters) {
        FileChooser fileChooser = new FileChooser(parent, mode, filters);
        createStartThread(fileChooser::init);
        return fileChooser;
    }

    public static FileChooser createFileChooser(Component parent, FileFilter... filters) {
        return createFileChooser(parent, null, filters);
    }

    public static FileChooser createFileChooser(Component parent, FileChooser.Mode mode) {
        return createFileChooser(parent, mode, null);
    }

    public static FileChooser createFileChooser(Component parent) {
        return createFileChooser(parent, null, null);
    }

    private static Thread createStartThread(Runnable runn) {
        Thread t = new Thread(runn); t.start();
        return t;
    }

    public static class FileChooser {

        private volatile JFileChooser chooser;
        private volatile Component parent;

        private volatile Mode mode;

        private volatile ArrayList<FileChooserCloseListener> closeListeners = new ArrayList<>();

        public enum Mode { FILE_SELECT, DIRECTORY_SELECT, SAVE_FILE_SELECT }

        public FileChooser(Component parent, Mode mode, FileFilter... filters) {

            if(mode == null) mode = Mode.FILE_SELECT;

            chooser = new JFileChooser();

            this.parent = parent;
            this.mode = mode;

            if(mode == Mode.DIRECTORY_SELECT) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // disable the "All files" option.
                chooser.setAcceptAllFileFilterUsed(false);
            }

            if(filters != null) {
                for(FileFilter filter : filters) {
                    chooser.addChoosableFileFilter(filter);
                }
            }

        }

        protected void init() {

            int returnVal;

            if(mode == Mode.SAVE_FILE_SELECT) {
                returnVal = chooser.showSaveDialog(parent);
            } else {
                returnVal = chooser.showOpenDialog(parent);
            }

            executeCloseListeners(returnVal, chooser.getSelectedFile());

        }

        public void addCloseListener(FileChooserCloseListener listener) {
            this.closeListeners.add(listener);
        }

        private void executeCloseListeners(int OPTION, File selectedFile) {
            for(FileChooserCloseListener listener : closeListeners) {
                listener.onClose(OPTION, selectedFile);
            }
        }

        public void close() {
            chooser.setVisible(false);
            executeCloseListeners(JFileChooser.CANCEL_OPTION, null);
        }

        public interface FileChooserCloseListener {
            void onClose(int OPTION, File selectedFile);
        }

    }

}
