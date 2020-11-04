package com.github.serivesmejia.eocvsim.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.serivesmejia.eocvsim.gui.util.GuiUtil;
import com.github.serivesmejia.eocvsim.gui.util.LineWrapRenderer;
import com.github.serivesmejia.eocvsim.gui.util.SourcesListIconRenderer;
import com.github.serivesmejia.eocvsim.input.InputSource;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import com.github.serivesmejia.eocvsim.util.Log;

public class Visualizer {

	public JFrame frame = new JFrame();
	public volatile JLabel img = new JLabel();
	
	public JScrollPane imgScrollPane = null;
	public JPanel imgScrollContainer = new JPanel();
	public JPanel rightContainer = new JPanel();
	public JSplitPane splitPane = null;
	
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
	
	private String title = "EasyOpenCV Simulator";
	private String titleMsg = "No pipeline";
	
	private String beforeTitle = "";
	private String beforeTitleMsg = "";

	private String beforeSelectedSource = "";
	private int beforeSelectedPipeline = -1;

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
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imgScrollPane, rightContainer);

		splitPane.setResizeWeight(1);
		splitPane.setOneTouchExpandable(false);
		splitPane.setContinuousLayout(true);

		frame.add(splitPane, BorderLayout.CENTER);

		frame.setSize(780, 645);
		frame.setMinimumSize(frame.getSize());
		frame.setTitle("EasyOpenCV Simulator - No Pipeline");

		frame.setIconImage(ICO_EOCVSIM.getImage());

	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitPane.setDividerLocation(1070);

		frame.setVisible(true);

		registerListeners();

	}


	private void registerListeners() {

		//listener for changing pause state
		pipelinePauseBtt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean selected = pipelinePauseBtt.isSelected();

				pipelineSelector.setEnabled(!selected);
				sourceSelector.setEnabled(!selected);

				pipelineSelector.revalidate();
				pipelineSelectorScroll.revalidate();

				sourceSelector.revalidate();
				sourceSelectorScroll.revalidate();

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
					if (!evt.getValueIsAdjusting() && !eocvSim.pipelineManager.isPaused() && pipeline != beforeSelectedPipeline) {
						eocvSim.pipelineManager.requestChangePipeline(pipeline);
						beforeSelectedPipeline = pipeline;
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

						if (!evt.getValueIsAdjusting() && !eocvSim.pipelineManager.isPaused() && !source.equals(beforeSelectedSource)) {
							eocvSim.inputSourceManager.requestSetInputSource(source);
							beforeSelectedSource = source;
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
		sourceSelectorDeleteBtt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String source = sourceSelector.getModel().getElementAt(sourceSelector.getSelectedIndex());
				eocvSim.runOnMainThread(new Runnable() {
					@Override
					public void run() {
						eocvSim.inputSourceManager.deleteInputSource(source);
						updateSourcesList();
					}
				});
			}
		});

	}

	public void updateVisualizedMat(Mat mat) {
		
		try {
			ImageIcon icon = new ImageIcon(CvUtil.matToBufferedImage(mat));
			img.setIcon(icon);
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
		if(beforeTitle != title) setFrameTitle(title, titleMsg);
		beforeTitle = title;
	}
	
	public void setTitleMessage(String titleMsg) {
		this.titleMsg = titleMsg;
		if(beforeTitleMsg != title) setFrameTitle(title, titleMsg);
		beforeTitleMsg = titleMsg;
	}
	
	public void updatePipelinesList() {
		
	    DefaultListModel<String> listModel = new DefaultListModel<>();  
        
		for(Class<OpenCvPipeline> pipelineClass : eocvSim.pipelineManager.pipelines) {
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
				listModel.addElement("<html>" + line + "</html>");
			}

			telemetryList.setModel(listModel);

			telemetryList.setFixedCellWidth(240);
			telemetryList.revalidate();
			telemetryScroll.revalidate();

		}

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