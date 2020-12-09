package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;

import javax.swing.*;

public class About {

    public JDialog about = null;

    public About(JFrame parent, EOCVSim eocvSim) {

        about = new JDialog(parent);

        eocvSim.visualizer.childDialogs.add(about);
        initAbout();

    }

    private void initAbout() {

    }

}
