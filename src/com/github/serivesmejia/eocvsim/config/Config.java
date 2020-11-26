package com.github.serivesmejia.eocvsim.config;

import com.github.serivesmejia.eocvsim.gui.theme.Theme;

public class Config {
    public volatile Theme simTheme = Theme.Default;
    public volatile double zoom = 1;
    public volatile boolean storeZoom = true;
    public volatile boolean pauseOnImages = true;
}