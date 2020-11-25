package com.github.serivesmejia.eocvsim.gui.theme;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

public class ThemeInstaller {

    private Theme installedTheme;

    public void installTheme(Theme theme) {

        switch(theme) {
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
        return installedTheme != Theme.Default && installedTheme != Theme.Light && installedTheme != Theme.Intellij;
    }

}
