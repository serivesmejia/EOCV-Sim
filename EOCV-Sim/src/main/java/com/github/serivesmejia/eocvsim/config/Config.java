package com.github.serivesmejia.eocvsim.config;

import com.github.serivesmejia.eocvsim.gui.theme.Theme;
import org.opencv.core.Size;

public class Config {
    public volatile Theme simTheme = Theme.Light;

    public volatile double zoom = 1;
    public volatile boolean storeZoom = true;

    public volatile int maxFps = 30;
    public volatile boolean pauseOnImages = true;

    public volatile Size videoRecordingSize = new Size(640, 480);
}