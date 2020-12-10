package com.github.serivesmejia.eocvsim.gui.dialog;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.gui.component.ImageX;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.util.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        about.setSize(400, 250);

        JPanel contents = new JPanel(new GridLayout(2, 1));
        contents.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageX icon = new ImageX(Visualizer.ICO_EOCVSIM);
        icon.setSize(50, 50);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel appInfo = new JLabel("EasyOpenCV Simulator v" + EOCVSim.VERSION);
        appInfo.setFont(appInfo.getFont().deriveFont(appInfo.getFont().getStyle() | Font.BOLD)); //set font to bold

        JPanel appInfoLogo = new JPanel(new FlowLayout());

        appInfoLogo.add(icon);
        appInfoLogo.add(appInfo);

        appInfoLogo.setBorder(BorderFactory.createEmptyBorder(20, 10, -40, 10));

        contents.add(appInfoLogo);

        JPanel contributors = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JList<String> contribsList = new JList<>();
        contribsList.setModel(CONTRIBS_LIST_MODEL);
        contribsList.setLayout(new FlowLayout(FlowLayout.CENTER));
        contribsList.setAlignmentY(Component.TOP_ALIGNMENT);

        contribsList.setVisibleRowCount(4);

        JPanel contributorsList = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contributorsList.setAlignmentY(Component.TOP_ALIGNMENT);

        JScrollPane contribsListScroll = new JScrollPane();
        contribsListScroll.setBorder(new EmptyBorder(0,0,20,10));
        contribsListScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contribsListScroll.setAlignmentY(Component.TOP_ALIGNMENT);
        contribsListScroll.setViewportView(contribsList);

        contributors.setAlignmentY(Component.TOP_ALIGNMENT);
        contents.setAlignmentY(Component.TOP_ALIGNMENT);

        contributorsList.add(contribsListScroll);
        contributors.add(contributorsList);
        contents.add(contributors);

        contents.setBorder(new EmptyBorder(10,10,10,10));

        about.add(contents);

        about.setResizable(false);

        about.setLocationRelativeTo(null);
        about.setVisible(true);

    }

}
