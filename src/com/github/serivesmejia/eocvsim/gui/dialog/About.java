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
    public static ListModel<String> OSL_LIST_MODEL;

    static {
        try {
            CONTRIBS_LIST_MODEL = GuiUtil.isToListModel(About.class.getResourceAsStream("/resources/contributors.txt"), StandardCharsets.UTF_8);
            OSL_LIST_MODEL = GuiUtil.isToListModel(About.class.getResourceAsStream("/resources/opensourcelibs.txt"), StandardCharsets.UTF_8);
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
        about.setSize(400, 300);

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

        appInfoLogo.setBorder(BorderFactory.createEmptyBorder(10, 10, -30, 10));

        contents.add(appInfoLogo);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel contributors = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JList<String> contribsList = new JList<>();
        contribsList.setModel(CONTRIBS_LIST_MODEL);
        contribsList.setSelectionModel(new GuiUtil.NoSelectionModel());
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

        tabbedPane.addTab("Contributors", contributors);

        JPanel osLibs = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JList<String> osLibsList = new JList<>();
        osLibsList.setModel(OSL_LIST_MODEL);
        osLibsList.setSelectionModel(new GuiUtil.NoSelectionModel());
        osLibsList.setLayout(new FlowLayout(FlowLayout.CENTER));
        osLibsList.setAlignmentY(Component.TOP_ALIGNMENT);

        osLibsList.setVisibleRowCount(4);

        JPanel osLibsListPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        osLibsList.setAlignmentY(Component.TOP_ALIGNMENT);

        JScrollPane osLibsListScroll = new JScrollPane();
        osLibsListScroll.setBorder(new EmptyBorder(0,0,20,10));
        osLibsListScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        osLibsListScroll.setAlignmentY(Component.TOP_ALIGNMENT);
        osLibsListScroll.setViewportView(osLibsList);

        osLibs.setAlignmentY(Component.TOP_ALIGNMENT);

        osLibsListPane.add(osLibsListScroll);
        osLibs.add(osLibsListPane);

        tabbedPane.addTab("Open Source Libraries", osLibs);

        contents.add(tabbedPane);

        contents.setBorder(new EmptyBorder(10,10,10,10));

        about.add(contents);

        about.setResizable(false);

        about.setLocationRelativeTo(null);
        about.setVisible(true);

    }

}
