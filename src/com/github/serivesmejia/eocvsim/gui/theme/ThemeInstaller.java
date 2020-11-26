package com.github.serivesmejia.eocvsim.gui.theme;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

public class ThemeInstaller {

    private Theme installedTheme;

    public void installTheme(Theme theme) {

        switch(theme) {
            case Default:
                break;
            case Light:
                FlatLightLaf.install();
                break;
            case Intellij:
                FlatIntelliJLaf.install();
                break;
            case Dark:
                FlatDarkLaf.install();
                break;
            case Darcula:
                FlatDarculaLaf.install();
                break;
            case Material_Dark_Intellij:
                FlatMaterialDesignDarkIJTheme.install();
                break;
        }

        installedTheme = theme;

    }

    public Theme getInstalledTheme() {
        return installedTheme;
    }

    public boolean isInstalledThemeDark() {
        return FlatLaf.isLafDark();
    }

}