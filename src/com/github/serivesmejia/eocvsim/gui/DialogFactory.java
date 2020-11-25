package com.github.serivesmejia.eocvsim.gui;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.input.InputSourceManager;

import java.util.Objects;

public class DialogFactory {

    private EOCVSim eocvSim;

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
        return createStartThread(() -> {
            new Configuration(eocvSim.visualizer.frame, eocvSim);
        });
    }

    private Thread createStartThread(Runnable runn) {
        Thread t = new Thread(runn); t.start();
        return t;
    }

}
