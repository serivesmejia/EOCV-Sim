package com.github.serivesmejia.eocvsim.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.gui.util.SourcesListIconRenderer;
import com.github.serivesmejia.eocvsim.input.InputSource;
import com.github.serivesmejia.eocvsim.pipeline.PipelineManager;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import com.github.serivesmejia.eocvsim.util.Log;

public class Visualizer {

	public JFrame frame = new JFrame();
	public volatile JLabel img = new JLabel();

	public JPanel tunerMenuPanel = new JPanel();

	public JScrollPane imgScrollPane = null;
	public JPanel imgScrollContainer = new JPanel();

	public JPanel rightContainer = new JPanel();

	public JSplitPane globalSplitPane = null;
	public JSplitPane imageTunerSplitPane = null;

	public JPanel pipelineSelectorContainer = new JPanel();
	public volatile JList<String> pipelineSelector = new JList<>();
	public JScrollPane pipelineSelectorScroll = new JScrollPane();
	public JPanel pipelineButtonsContainer = new JPanel();
	public JToggleButton pipelinePauseBtt =  new JToggleButton("Pause");

	public JPanel sourceSelectorContainer = new JPanel();
	public volatile JList<String> sourceSelector = new JList<>();
	public JScrollPane sourceSelectorScroll = new JScrollPane();
	public JPanel sourceSelectorButtonsContainer = new JPanel();
	public JButton sourceSelectorCreateBtt = new JButton("Create");
	public JButton sourceSelectorDeleteBtt = new JButton("Delete");

	public JPanel telemetryContainer = new JPanel();
    public JScrollPane telemetryScroll = new JScrollPane();
    public volatile JList<String> telemetryList = new JList<>();

	private EOCVSim eocvSim = null;
	
	private String title = "EasyOpenCV Simulator v" + EOCVSim.VERSION;
	private String titleMsg = "No pipeline";
	
	private String beforeTitle = "";
	private String beforeTitleMsg = "";

	private String beforeSelectedSource = "";
	private int beforeSelectedSourceIndex = 0;

	private int beforeSelectedPipeline = -1;

	//stuff for zooming handling
	private volatile double scale = 1f;

	private volatile BufferedImage lastMatBufferedImage = null;
    private volatile Point mousePosition = new Point(0, 0);
    private volatile Point lastMousePosition = new Point(0, 0);

    private volatile boolean isCtrlPressed = false;

	public static ImageIcon ICO_EOCVSIM = null;

	static {
		try {
			ICO_EOCVSIM = GuiUtil.loadImageIcon("/resources/images/icon/ico_eocvsim.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Visualizer(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}
	
	public void init() {
		
		rightContainer = new JPanel();

		/*
		* IMG VISUALIZER & SCROLL PANE
		*/

		imgScrollContainer = new JPanel();
		imgScrollPane = new JScrollPane(imgScrollContainer);

		imgScrollContainer.setLayout(new GridBagLayout());

		imgScrollContainer.add(img, new GridBagConstraints());

		imgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		imgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		imgScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		imgScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		rightContainer.setLayout(new GridLayout(3, 1));

		/*
		* PIPELINE SELECTOR
		*/

		pipelineSelectorContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		//pipelineSelectorContainer.setBorder(BorderFactory.createLineBorder(Color.black));

		JLabel pipelineSelectorLabel = new JLabel("Pipelines");

		pipelineSelectorLabel.setFont(pipelineSelectorLabel.getFont().deriveFont(20.0f));

		pipelineSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
		pipelineSelectorContainer.add(pipelineSelectorLabel);

		pipelineSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel pipelineSelectorScrollContainer = new JPanel();
		pipelineSelectorScrollContainer.setLayout(new GridLayout());
		pipelineSelectorScrollContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		pipelineSelectorScrollContainer.add(pipelineSelectorScroll);

		pipelineSelectorScroll.setViewportView(pipelineSelector);
		pipelineSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pipelineSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		pipelineSelectorContainer.add(pipelineSelectorScrollContainer);

		pipelineButtonsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));

		pipelineButtonsContainer.add(pipelinePauseBtt);

		pipelineSelectorContainer.add(pipelineButtonsContainer);

		rightContainer.add(pipelineSelectorContainer);

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

		try {
			sourceSelector.setCellRenderer(new SourcesListIconRenderer(eocvSim.inputSourceManager));
		} catch (IOException e) {
			e.printStackTrace();
		}

		sourceSelectorCreateBtt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(CreateSource.alreadyOpened) return;
				new CreateSource(frame, eocvSim);
			}
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
			public void mouseDragged(MouseEvent e) {}

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

		frame.setIconImage(ICO_EOCVSIM.getImage());

	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		globalSplitPane.setDividerLocation(1070);

		frame.setVisible(true);

		registerListeners();

	}


	private void registerListeners() {

		//listener for changing pause state
		pipelinePauseBtt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean selected = pipelinePauseBtt.isSelected();

				eocvSim.runOnMainThread(new Runnable() {
					@Override
					public void run() {
						eocvSim.pipelineManager.setPaused(selected);
					}
				});

			}

		});

		//listener for changing pipeline
		pipelineSelector.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(pipelineSelector.getSelectedIndex() != -1) {

					int pipeline = pipelineSelector.getSelectedIndex();

					if (!evt.getValueIsAdjusting() &&  pipeline != beforeSelectedPipeline) {
						if(!eocvSim.pipelineManager.isPaused()) {
							eocvSim.pipelineManager.requestChangePipeline(pipeline);
							beforeSelectedPipeline = pipeline;
						} else {
							if(eocvSim.pipelineManager.getPauseReason() != PipelineManager.PauseReason.IMAGE_ONE_ANALYSIS) {
								pipelineSelector.setSelectedIndex(beforeSelectedPipeline);
							} else { //handling pausing
								eocvSim.pipelineManager.requestSetPaused(false);
								eocvSim.pipelineManager.requestChangePipeline(pipeline);
								beforeSelectedPipeline = pipeline;
							}
						}
					}

				} else {
					pipelineSelector.setSelectedIndex(1);
				}
			}

		});

		//listener for changing input sources
		sourceSelector.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {

				try {
					if(sourceSelector.getSelectedIndex() != -1) {

						ListModel<String> model = sourceSelector.getModel();
						String source = model.getElementAt(sourceSelector.getSelectedIndex());

						if (!evt.getValueIsAdjusting() && !source.equals(beforeSelectedSource)) {
							if(!eocvSim.pipelineManager.isPaused()) {
								eocvSim.inputSourceManager.requestSetInputSource(source);
								beforeSelectedSource = source;
								beforeSelectedSourceIndex = sourceSelector.getSelectedIndex();
							} else {
								if(eocvSim.pipelineManager.getPauseReason() != PipelineManager.PauseReason.IMAGE_ONE_ANALYSIS) {
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
				} catch(ArrayIndexOutOfBoundsException ex) { }

			}

		});

		//handling onViewportTapped evts
		img.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				eocvSim.pipelineManager.currentPipeline.onViewportTapped();
			}
		});

		// delete input source
		sourceSelectorDeleteBtt.addActionListener(e -> {
			String source = sourceSelector.getModel().getElementAt(sourceSelector.getSelectedIndex());
			eocvSim.runOnMainThread(() -> {
				eocvSim.inputSourceManager.deleteInputSource(source);
				updateSourcesList();
			});
		});

		//RESIZE HANDLING
        imgScrollPane.addMouseWheelListener(e -> eocvSim.runOnMainThread(() -> {
			if(isCtrlPressed) { //check if control key is pressed

				lastMousePosition = mousePosition;

				scale -= 0.5 * e.getPreciseWheelRotation();
				if(scale <= 0) scale = 0.5;

				scaleAndZoom(lastMousePosition);

			}
		}));

        imgScrollPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) { }
            @Override
            public void mouseMoved(MouseEvent e) {
                PointerInfo info = MouseInfo.getPointerInfo();
                mousePosition = info.getLocation();
            }
        });

        //listening for keyboard presses and releases, to check if ctrl key was pressed or released
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
			switch (ke.getID()) {
				case KeyEvent.KEY_PRESSED:
					if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
						isCtrlPressed = true;
						imgScrollPane.setWheelScrollingEnabled(false); //lock scrolling if ctr is pressed
					}
					break;
				case KeyEvent.KEY_RELEASED:
					if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
						isCtrlPressed = false;
						imgScrollPane.setWheelScrollingEnabled(true); //unlock
					}
					break;
			}
			return true; //idk let's just return true
		});

    }

    //scale img
    private void scaleAndZoom(Point point) {

		double multiplier = (320f/240f) / ((double) lastMatBufferedImage.getHeight() / (double) lastMatBufferedImage.getHeight());
		multiplier = Math.abs(multiplier);

		if(scale >= 1.5 * multiplier) scale = 1.5 * multiplier;
		if(scale <= 0) scale = 0.5;

		System.out.println(scale);

        Rectangle view = imgScrollPane.getViewport().getViewRect();

        int moveX = point.x;
        int moveY = point.y;

        view.setBounds(view.x+moveX,view.y+moveY, view.width, view.height);

        ImageIcon icon = new ImageIcon(GuiUtil.scaleImage(lastMatBufferedImage, scale));
        img.setIcon(icon);

    }

	public void updateVisualizedMat(Mat mat) {
		
		try {

		    lastMatBufferedImage = CvUtil.matToBufferedImage(mat);

            scaleAndZoom(lastMousePosition);

		} catch(Throwable ex) {
			Log.error("Visualizer", "Couldn't visualize last mat: (" + ex.toString() + ")");
		}
		
		mat.release();
		
	}
	
	private void setFrameTitle(String title, String titleMsg) {
		frame.setTitle(title + " - " + titleMsg);
	}
	
	public void setTitle(String title) {
		this.title = title;
		if(!beforeTitle.equals(title)) setFrameTitle(title, titleMsg);
		beforeTitle = title;
	}
	
	public void setTitleMessage(String titleMsg) {
		this.titleMsg = titleMsg;
		if(!beforeTitleMsg.equals(title)) setFrameTitle(title, titleMsg);
		beforeTitleMsg = titleMsg;
	}
	
	public void updatePipelinesList() {
		
	    DefaultListModel<String> listModel = new DefaultListModel<>();  
        
		for(Class<? extends OpenCvPipeline> pipelineClass : eocvSim.pipelineManager.pipelines) {
			listModel.addElement(pipelineClass.getSimpleName());
		}
		
		pipelineSelector.setFixedCellWidth(240);
		
		pipelineSelector.setModel(listModel);
		pipelineSelector.revalidate();
		pipelineSelectorScroll.revalidate();
		
	}
	
	public void updateSourcesList() {
		
	    DefaultListModel<String> listModel = new DefaultListModel<>();  
        
		for(Map.Entry<String, InputSource> entry : eocvSim.inputSourceManager.sources.entrySet()) {
			listModel.addElement(entry.getKey());
		}
		
		sourceSelector.setFixedCellWidth(240);

		sourceSelector.setModel(listModel);
		sourceSelector.revalidate();
		sourceSelectorScroll.revalidate();
		
	}

	public void updateTelemetry(Telemetry telemetry) {

		if(telemetry != null && telemetry.hasChanged()) {

			DefaultListModel<String> listModel = new DefaultListModel<>();

			for(String line : telemetry.toString().split("\n")) {
				listModel.addElement(line);
			}

			telemetryList.setFixedCellWidth(240);

			telemetryList.setModel(listModel);
			telemetryList.revalidate();
			telemetryScroll.revalidate();

		}

	}

	public void updateTunerFields(List<TunableFieldPanel> fields) {

		tunerMenuPanel.removeAll();

		for(TunableFieldPanel fieldPanel : fields) {
			tunerMenuPanel.add(fieldPanel);
		}

		tunerMenuPanel.updateUI();

	}

	// PLEASE WAIT DIALOGS

	public boolean pleaseWaitDialog(JDialog diag, String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable, AsyncPleaseWaitDialog apwd, boolean isError) {

		final JDialog dialog = diag == null ? new JDialog(this.frame) : diag;

		boolean addSubMessage = subMessage != null;

		int rows = 3;
		if(!addSubMessage) { rows--; }

		dialog.setModal(true);
		dialog.setLayout(new GridLayout(rows, 1));

		if(isError) {
			dialog.setTitle("Operation failed");
		} else {
			dialog.setTitle("Operation in progress");
		}

		JLabel msg = new JLabel(message);
		msg.setHorizontalAlignment(JLabel.CENTER);
		msg.setVerticalAlignment(JLabel.CENTER);

		dialog.add(msg);

		JLabel subMsg = null;
		if(addSubMessage) {

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

		cancelBtt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled[0] = true;
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

		dialog.add(exitBttPanel);

		if(apwd != null) {
			apwd.msg = msg;
			apwd.subMsg = subMsg;
			apwd.cancelBtt = cancelBtt;
		}

		if(size != null) {
			dialog.setSize(size);
		} else {
			dialog.setSize(new Dimension(400, 200));
		}

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

		AsyncPleaseWaitDialog rPWD = new AsyncPleaseWaitDialog(message, subMessage, cancelBttText, size, cancellable, isError);

		new Thread(rPWD).start();

		return rPWD;

	}

	public AsyncPleaseWaitDialog asyncPleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable) {

		AsyncPleaseWaitDialog rPWD = new AsyncPleaseWaitDialog(message, subMessage, cancelBttText, size, cancellable, false);

		new Thread(rPWD).start();

		return rPWD;

	}

	public class AsyncPleaseWaitDialog implements Runnable {

		String message = "";
		String subMessage = "";
		String cancelBttText = "";
		Dimension size = null;
		boolean cancellable = false;

		public volatile JDialog dialog = new JDialog(frame);
		public volatile JLabel msg = null;
		public volatile JLabel subMsg = null;
		public volatile JButton cancelBtt = null;

		public volatile boolean wasCancelled = false;

		public volatile boolean isError = false;

		public volatile String initialMessage = "";
		public volatile String initialSubMessage = "";

		private ArrayList<Runnable> onCancelRunnables = new ArrayList<Runnable>();

		public AsyncPleaseWaitDialog(String message, String subMessage, String cancelBttText, Dimension size, boolean cancellable, boolean isError) {

			this.message = message;
			this.subMessage = subMessage;
			this.initialMessage = message;
			this.initialSubMessage = subMessage;
			this.cancelBttText = cancelBttText;

			this.size = size;
			this.cancellable = cancellable;

			this.isError = isError;

		}

		public void onCancel(Runnable runn) {
			onCancelRunnables.add(runn);
		}

		@Override
		public void run() {

			wasCancelled = pleaseWaitDialog(dialog, message, subMessage, cancelBttText, size, cancellable, this, isError);

			if(wasCancelled) {
				for(Runnable runn : onCancelRunnables) {
					runn.run();
				}
			}

		}

		public void destroyDialog() {
			dialog.setVisible(false);
			dialog.dispose();
		}

	}

}