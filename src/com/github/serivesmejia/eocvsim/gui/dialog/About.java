package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.gui.component.ImageX;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class About {

    public JDialog about = null;

    public static ListModel<String> CONTRIBS_LIST_MODEL;

    static {
        try {
            CONTRIBS_LIST_MODEL = GuiUtil.isToListModel(About.class.getResourceAsStream("/resources/contributors.txt"), StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public About(JFrame parent, EOCVSim eocvSim) {

        about = new JDialog(parent);

        eocvSim.visualizer.childDialogs.add(about);
        initAbout();

    }

    private void initAbout() {

        about.setModal(true);

        about.setTitle("About");
        about.setSize(440, 300);

        JPanel contents = new JPanel(new GridLayout(2, 1));

        ImageX icon = new ImageX(Visualizer.ICO_EOCVSIM);
        icon.setSize(50, 50);

        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel appInfo = new JLabel("EasyOpenCV Simulator v" + EOCVSim.VERSION);
        appInfo.setFont(appInfo.getFont().deriveFont(appInfo.getFont().getStyle() | Font.BOLD)); //set font to bold

        JPanel appInfoLogo = new JPanel(new FlowLayout());

        appInfoLogo.add(icon);
        appInfoLogo.add(appInfo);

        appInfoLogo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        contents.add(appInfoLogo);

        JPanel contributors = new JPanel(new FlowLayout(FlowLayout.LEADING));

        JList<String> contribsList = new JList<>();
        contribsList.setModel(CONTRIBS_LIST_MODEL);

        JPanel contributorsList = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JScrollPane contribsListScroll = new JScrollPane();
        contribsListScroll.setViewportView(contribsList);

        contributorsList.add(contribsListScroll);
        contributors.add(contributorsList);
        contents.add(contributors);

        contents.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        about.add(contents);

        about.setResizable(false);
        about.setLocationRelativeTo(null);
        about.setVisible(true);

    }

}
