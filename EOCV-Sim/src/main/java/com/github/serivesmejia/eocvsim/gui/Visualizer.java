/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.serivesmejia.eocvsim.gui;

import com.formdev.flatlaf.FlatLaf;
import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.component.visualizer.CreateSourcePanel;
import com.github.serivesmejia.eocvsim.gui.component.PopupX;
import com.github.serivesmejia.eocvsim.gui.component.Viewport;
import com.github.serivesmejia.eocvsim.gui.component.tuner.ColorPicker;
import com.github.serivesmejia.eocvsim.gui.component.tuner.TunableFieldPanel;
import com.github.serivesmejia.eocvsim.gui.component.visualizer.PipelineSelector;
import com.github.serivesmejia.eocvsim.gui.component.visualizer.TopMenuBar;
import com.github.serivesmejia.eocvsim.gui.theme.Theme;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.gui.util.SourcesListIconRenderer;
import com.github.serivesmejia.eocvsim.input.InputSource;
import com.github.serivesmejia.eocvsim.input.SourceType;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import com.github.serivesmejia.eocvsim.util.Log;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.easyopencv.OpenCvPipeline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Visualizer {

    public static ImageIcon ICO_EOCVSIM = null;

    static {
        try {
            ICO_EOCVSIM = GuiUtil.loadImageIcon("/images/icon/ico_eocvsim.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final ArrayList<AsyncPleaseWaitDialog> pleaseWaitDialogs = new ArrayList<>();

    public final ArrayList<JFrame> childFrames = new ArrayList<>();
    public final ArrayList<JDialog> childDialogs = new ArrayList<>();

    private final ArrayList<Runnable> onInitFinishedRunns = new ArrayList<>();

    private final EOCVSim eocvSim;

    public JFrame frame = null;

    public Viewport viewport = null;

    public TopMenuBar menuBar = null;

    public JPanel tunerMenuPanel = new JPanel();

    public JScrollPane imgScrollPane = null;

    public JPanel rightContainer = null;
    public JSplitPane globalSplitPane = null;
    public JSplitPane imageTunerSplitPane = null;

    public PipelineSelector pipelineSelector = null;

    public JPanel sourceSelectorContainer = null;
    public volatile JList<String> sourceSelector = null;
    public JScrollPane sourceSelectorScroll = null;
    public JPanel sourceSelectorButtonsContainer = null;
    public JButton sourceSelectorCreateBtt = null;
    public JButton sourceSelectorDeleteBtt = null;

    public JPanel telemetryContainer = null;
    public JScrollPane telemetryScroll = null;
    public volatile JList<String> telemetryList = null;

    private String title = "EasyOpenCV Simulator v" + EOCVSim.VERSION;
    private String titleMsg = "No pipeline";
    private String beforeTitle = "";
    private String beforeTitleMsg = "";

    private String beforeSelectedSource = "";

    private int beforeSelectedSourceIndex = 0;
    private int beforeSelectedPipeline = -1;

    public ColorPicker colorPicker = null;

    //stuff for zooming handling
    private volatile boolean isCtrlPressed = false;

    private volatile boolean hasFinishedInitializing = false;

    private PopupX lastCreateSourcePopup = null;

    public Visualizer(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public void init(Theme theme) {

        if(Taskbar.isTaskbarSupported()){
            try {
                //set icon for mac os (and other systems which do support this method)
                Taskbar.getTaskbar().setIconImage(ICO_EOCVSIM.getImage());
            } catch (final UnsupportedOperationException ignored) {
            } catch (final SecurityException e) {
                Log.error("Visualizer", "Security exception while setting TaskBar icon", e);
            }
        }

        try {
            theme.install();
        } catch (Exception e) {
            Log.error("Visualizer", "Failed to set theme " + theme.name(), e);
        }

        Icons.INSTANCE.setDark(FlatLaf.isLafDark());

        //instantiate all swing elements after theme installation
        frame = new JFrame();
        viewport = new Viewport(eocvSim, eocvSim.getConfig().maxFps);

        menuBar = new TopMenuBar(this, eocvSim);

        tunerMenuPanel = new JPanel();

        pipelineSelector = new PipelineSelector(eocvSim);
        
        sourceSelectorContainer = new JPanel();
        sourceSelector = new JList<>();
        sourceSelectorScroll = new JScrollPane();
        sourceSelectorButtonsContainer = new JPanel();
        sourceSelectorCreateBtt = new JButton("Create");
        sourceSelectorDeleteBtt = new JButton("Delete");

        telemetryContainer = new JPanel();
        telemetryScroll = new JScrollPane();
        telemetryList = new JList<>();

        rightContainer = new JPanel();

        /*
         * TOP MENU BAR
         */
        
        frame.setJMenuBar(menuBar);

        /*
         * IMG VISUALIZER & SCROLL PANE
         */

        imgScrollPane = new JScrollPane(viewport);

        imgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        imgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        imgScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        imgScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightContainer.setLayout(new GridLayout(3, 1));

        /*
         * PIPELINE SELECTOR
         */

        rightContainer.add(pipelineSelector);

        /*
         * SOURCE SELECTOR
         */

        sourceSelectorContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        //sourceSelectorContainer.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel sourceSelectorLabel = new JLabel("Sources");

        sourceSelectorLabel.setFont(sourceSelectorLabel.getFont().deriveFont(20.0f));

        sourceSelectorLabel.setHorizontalAlignment(JLabel.CENTER);

        sourceSelectorContainer.add(sourceSelectorLabel);

        JPanel sourceSelectorScrollContainer = new JPanel();
        sourceSelectorScrollContainer.setLayout(new GridLayout());
        sourceSelectorScrollContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        sourceSelectorScrollContainer.add(sourceSelectorScroll);

        sourceSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sourceSelectorScroll.setViewportView(sourceSelector);
        sourceSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sourceSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //different icons
        sourceSelector.setCellRenderer(new SourcesListIconRenderer(eocvSim.inputSourceManager));

        sourceSelectorCreateBtt.addActionListener(e -> {
            if(lastCreateSourcePopup != null) {
                lastCreateSourcePopup.hide();
            }
            CreateSourcePanel panel = new CreateSourcePanel(eocvSim);

            int buttonHeight = sourceSelectorCreateBtt.getHeight();
            Point location   = sourceSelectorCreateBtt.getLocationOnScreen();

            PopupX popup = new PopupX(frame, panel, location.x, location.y, true);

            lastCreateSourcePopup = popup;
            popup.show();
        });

        sourceSelectorContainer.add(sourceSelectorScrollContainer);

        sourceSelectorButtonsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));

        sourceSelectorButtonsContainer.add(sourceSelectorCreateBtt);
        sourceSelectorButtonsContainer.add(sourceSelectorDeleteBtt);

        sourceSelectorContainer.add(sourceSelectorButtonsContainer);

        rightContainer.add(sourceSelectorContainer);

        /*
         * TELEMETRY
         */

        telemetryContainer.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel telemetryLabel = new JLabel("Telemetry");

        telemetryLabel.setFont(telemetryLabel.getFont().deriveFont(20.0f));
        telemetryLabel.setHorizontalAlignment(JLabel.CENTER);

        telemetryContainer.add(telemetryLabel);

        telemetryScroll.setViewportView(telemetryList);
        telemetryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        telemetryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //tooltips for the telemetry list items (thnx stackoverflow)
        telemetryList.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) { }

            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();

                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    l.setToolTipText(m.getElementAt(index).toString());
                }
            }
        });

        telemetryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel telemetryScrollContainer = new JPanel();
        telemetryScrollContainer.setLayout(new GridLayout());
        telemetryScrollContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        telemetryScrollContainer.add(telemetryScroll);

        telemetryContainer.add(telemetryScrollContainer);

        rightContainer.add(telemetryContainer);

        /*
         * SPLIT
         */

        //left side, image scroll & tuner menu split panel
        imageTunerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imgScrollPane, tunerMenuPanel);

        imageTunerSplitPane.setResizeWeight(1);
        imageTunerSplitPane.setOneTouchExpandable(false);
        imageTunerSplitPane.setContinuousLayout(true);

        //global
        globalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageTunerSplitPane, rightContainer);

        globalSplitPane.setResizeWeight(1);
        globalSplitPane.setOneTouchExpandable(false);
        globalSplitPane.setContinuousLayout(true);

        frame.add(globalSplitPane, BorderLayout.CENTER);

        //initialize other various stuff of the frame
        frame.setSize(780, 645);
        frame.setMinimumSize(frame.getSize());
        frame.setTitle("EasyOpenCV Simulator - No Pipeline");

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.setIconImage(ICO_EOCVSIM.getImage());

        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        globalSplitPane.setDividerLocation(1070);

        frame.setVisible(true);

        registerListeners();

        colorPicker = new ColorPicker(viewport.image);

        for(Runnable runn : onInitFinishedRunns) {
            runn.run();
        }

        hasFinishedInitializing = true;
    }

    public void initAsync(Theme simTheme) {
        SwingUtilities.invokeLater(() -> init(simTheme));
    }

    private void registerListeners() {

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                eocvSim.onMainUpdate.doOnce((Runnable) eocvSim::destroy);
            }
        });

        //listener for changing input sources
        sourceSelector.addListSelectionListener(evt -> {
            try {
                if (sourceSelector.getSelectedIndex() != -1) {

                    ListModel<String> model = sourceSelector.getModel();
                    String source = model.getElementAt(sourceSelector.getSelectedIndex());

                    if (!evt.getValueIsAdjusting() && !source.equals(beforeSelectedSource)) {
                        if (!eocvSim.pipelineManager.getPaused()) {
                            eocvSim.inputSourceManager.requestSetInputSource(source);
                            beforeSelectedSource = source;
                            beforeSelectedSourceIndex = sourceSelector.getSelectedIndex();
                        } else {
                            //check if the user requested the pause or if it was due to one shoot analysis when selecting images
                            if (eocvSim.pipelineManager.getPauseReason() != PipelineManager.PauseReason.IMAGE_ONE_ANALYSIS) {
                                sourceSelector.setSelectedIndex(beforeSelectedSourceIndex);
                            } else { //handling pausing
                                eocvSim.pipelineManager.requestSetPaused(false);
                                eocvSim.inputSourceManager.requestSetInputSource(source);
                                beforeSelectedSource = source;
                                beforeSelectedSourceIndex = sourceSelector.getSelectedIndex();
                            }
                        }
                    }

                } else {
                    sourceSelector.setSelectedIndex(1);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) { }

        });

        //handling onViewportTapped evts
        viewport.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(!colorPicker.isPicking())
                    eocvSim.pipelineManager.callViewportTapped();
            }
        });

        // delete selected input source
        sourceSelectorDeleteBtt.addActionListener(e -> {
            String source = sourceSelector.getModel().getElementAt(sourceSelector.getSelectedIndex());
            eocvSim.onMainUpdate.doOnce(() -> {
                eocvSim.inputSourceManager.deleteInputSource(source);
                updateSourcesList();
            });
        });

        //VIEWPORT RESIZE HANDLING
        imgScrollPane.addMouseWheelListener(e -> {
            if (isCtrlPressed) { //check if control key is pressed
                double scale = viewport.getViewportScale() - (0.15 * e.getPreciseWheelRotation());
                viewport.setViewportScale(scale);
            }
        });

        //listening for keyboard presses and releases, to check if ctrl key was pressed or released (handling zoom)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED:
                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                        isCtrlPressed = true;
                        imgScrollPane.setWheelScrollingEnabled(false); //lock scrolling if ctrl is pressed
                    }
                    break;
                case KeyEvent.KEY_RELEASED:
                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                        isCtrlPressed = false;
                        imgScrollPane.setWheelScrollingEnabled(true); //unlock
                    }
                    break;
            }
            return false; //idk let's just return false 'cause keyboard input doesn't work otherwise
        });

        //resizes all three JLists in right panel to make buttons visible in smaller resolutions
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                double ratio = frame.getSize().getHeight() / 820;
                int columns = (int) Math.round(8 * ratio);

                pipelineSelector.getPipelineSelector().setVisibleRowCount(columns);

                //gotta revalidate and repaint
                //for every single involved element...
                //thanks swing, very cool
                pipelineSelector.revalAndRepaint();

                sourceSelector.setVisibleRowCount(columns);

                sourceSelector.revalidate();
                sourceSelector.repaint();
                sourceSelectorScroll.revalidate();
                sourceSelectorScroll.repaint();

                telemetryList.setVisibleRowCount(columns);

                telemetryList.revalidate();
                telemetryList.repaint();
                telemetryScroll.revalidate();
                telemetryScroll.repaint();

                rightContainer.revalidate();
                rightContainer.repaint();
            }
        });

        //stop color-picking mode when changing pipeline
        //eocvSim.pipelineManager.onPipelineChange.doPersistent(() -> colorPicker.stopPicking());

    }

    public void waitForFinishingInit() {
        while (!hasFinishedInitializing) {
            Thread.yield();
        }
    }

    public void onInitFinished(Runnable runn) {
        onInitFinishedRunns.add(runn);
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            viewport.stop();

            //close all asyncpleasewait dialogs
            for (AsyncPleaseWaitDialog dialog : pleaseWaitDialogs) {
                if (dialog != null) {
                    dialog.destroyDialog();
                }
            }

            pleaseWaitDialogs.clear();

            //close all opened frames
            for (JFrame frame : childFrames) {
                if (frame != null && frame.isVisible()) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            }

            childFrames.clear();

            //close all opened dialogs
            for (JDialog dialog : childDialogs) {
                if (dialog != null && dialog.isVisible()) {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }

            childDialogs.clear();
            frame.dispose();
            viewport.flush();
        });
    }

    private void setFrameTitle(String title, String titleMsg) {
        frame.setTitle(title + " - " + titleMsg);
    }

    public void setTitle(String title) {
        this.title = title;
        if (!beforeTitle.equals(title)) setFrameTitle(title, titleMsg);
        beforeTitle = title;
    }

    public void setTitleMessage(String titleMsg) {
        this.titleMsg = titleMsg;
        if (!beforeTitleMsg.equals(title)) setFrameTitle(title, titleMsg);
        beforeTitleMsg = titleMsg;
    }

    public void updateSourcesList() {
        //SwingUtilities.invokeLater(() -> {
            DefaultListModel<String> listModel = new DefaultListModel<>();

            for (InputSource source : eocvSim.inputSourceManager.getSortedInputSources()) {
                listModel.addElement(source.getName());
            }

            sourceSelector.setFixedCellWidth(240);

            sourceSelector.setModel(listModel);
            sourceSelector.revalidate();
            sourceSelectorScroll.revalidate();
        //});
    }

    public void updateTelemetry(Telemetry telemetry) {

        String[] telemetryText = {null};

        if (telemetry != null && telemetry.hasChanged()) {
            telemetryText[0] = telemetry.toString();

            //SwingUtilities.invokeLater(() -> {
                DefaultListModel<String> listModel = new DefaultListModel<>();

                for (String line : telemetryText[0].split("\n")) {
                    listModel.addElement(line);
                }

                telemetryList.setFixedCellWidth(240);

                telemetryList.setModel(listModel);
                telemetryList.revalidate();
                telemetryScroll.revalidate();
            //});

        }

        if(telemetryList.getModel().getSize() <= 0 || (telemetryText[0] != null && telemetryText[0].trim().equals(""))) {
            //SwingUtilities.invokeLater(() -> {
                DefaultListModel<String> listModel = new DefaultListModel<>();
                listModel.addElement("<html></html>");

                telemetryList.setModel(listModel);
            //});
        }

    }

    public void updateTunerFields(List<TunableFieldPanel> fields) {
        tunerMenuPanel.removeAll();

        for (TunableFieldPanel fieldPanel : fields) {
            tunerMenuPanel.add(fieldPanel);
            fieldPanel.showFieldPanel();
        }

        tunerMenuPanel.updateUI();
        imageTunerSplitPane.updateUI();
    }

    // PLEASE WAIT DIALOGS

    public boolean pleaseWaitDialog(JDialog diag, String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable, AsyncPleaseWaitDialog apwd, boolean isError) {

        final JDialog dialog = diag == null ? new JDialog(this.frame) : diag;

        boolean addSubMessage = subMessage != null;

        int rows = 3;
        if (!addSubMessage) {
            rows--;
        }

        dialog.setModal(true);
        dialog.setLayout(new GridLayout(rows, 1));

        if (isError) {
            dialog.setTitle("Operation failed");
        } else {
            dialog.setTitle("Operation in progress");
        }

        JLabel msg = new JLabel(message);
        msg.setHorizontalAlignment(JLabel.CENTER);
        msg.setVerticalAlignment(JLabel.CENTER);

        dialog.add(msg);

        JLabel subMsg = null;
        if (addSubMessage) {

            subMsg = new JLabel(subMessage);
            subMsg.setHorizontalAlignment(JLabel.CENTER);
            subMsg.setVerticalAlignment(JLabel.CENTER);

            dialog.add(subMsg);

        }

        JPanel exitBttPanel = new JPanel(new FlowLayout());
        JButton cancelBtt = new JButton(cancelBttText);

        cancelBtt.setEnabled(cancellable);

        exitBttPanel.add(cancelBtt);

        boolean[] cancelled = {false};

        cancelBtt.addActionListener(e -> {
            cancelled[0] = true;
            dialog.setVisible(false);
            dialog.dispose();
        });

        dialog.add(exitBttPanel);

        if (apwd != null) {
            apwd.msg = msg;
            apwd.subMsg = subMsg;
            apwd.cancelBtt = cancelBtt;
        }

        dialog.setSize(Objects.requireNonNullElseGet(size, () -> new Dimension(400, 200)));

        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.setVisible(true);

        return cancelled[0];
    }

    public void pleaseWaitDialog(JDialog dialog, String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable) {
        pleaseWaitDialog(dialog, message, subMessage, cancelBttText, size, cancellable, null, false);
    }

    public void pleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable) {
        pleaseWaitDialog(null, message, subMessage, cancelBttText, size, cancellable, null, false);
    }

    public AsyncPleaseWaitDialog asyncPleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable, boolean isError) {
        AsyncPleaseWaitDialog rPWD = new AsyncPleaseWaitDialog(message, subMessage, cancelBttText, size, cancellable, isError, eocvSim);
        SwingUtilities.invokeLater(rPWD);

        return rPWD;
    }

    public AsyncPleaseWaitDialog asyncPleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable) {
        AsyncPleaseWaitDialog rPWD = new AsyncPleaseWaitDialog(message, subMessage, cancelBttText, size, cancellable, false, eocvSim);
        SwingUtilities.invokeLater(rPWD);

        return rPWD;
    }

    public class AsyncPleaseWaitDialog implements Runnable {

        public volatile JDialog dialog = new JDialog(frame);

        public volatile JLabel msg = null;
        public volatile JLabel subMsg = null;

        public volatile JButton cancelBtt = null;

        public volatile boolean wasCancelled = false;
        public volatile boolean isError;

        public volatile String initialMessage;
        public volatile String initialSubMessage;

        public volatile boolean isDestroyed = false;

        String message;
        String subMessage;
        String cancelBttText;

        Dimension size;

        boolean cancellable;

        private final ArrayList<Runnable> onCancelRunnables = new ArrayList<>();

        public AsyncPleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable, boolean isError, EOCVSim eocvSim) {
            this.message = message;
            this.subMessage = subMessage;
            this.initialMessage = message;
            this.initialSubMessage = subMessage;
            this.cancelBttText = cancelBttText;

            this.size = size;
            this.cancellable = cancellable;

            this.isError = isError;

            eocvSim.visualizer.pleaseWaitDialogs.add(this);
        }

        public void onCancel(Runnable runn) {
            onCancelRunnables.add(runn);
        }

        @Override
        public void run() {
            wasCancelled = pleaseWaitDialog(dialog, message, subMessage, cancelBttText, size, cancellable, this, isError);

            if (wasCancelled) {
                for (Runnable runn : onCancelRunnables) {
                    runn.run();
                }
            }
        }

        public void destroyDialog() {
            if (!isDestroyed) {
                dialog.setVisible(false);
                dialog.dispose();
                isDestroyed = true;
            }
        }

    }

}
