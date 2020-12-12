package com.github.serivesmejia.eocvsim.gui.theme;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

import javax.swing.*;

public class ThemeInstaller {

    private Theme installedTheme;

    public void installTheme(Theme theme) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        switch (theme) {
            case Default:
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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