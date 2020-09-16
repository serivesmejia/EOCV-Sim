package com.github.serivesmejia.eocvsim.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.opencv.core.Mat;

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
	public JList<String> pipelineSelector = new JList<>();
	public JScrollPane pipelineSelectorScroll = new JScrollPane();
	
	public JPanel sourceSelectorContainer = new JPanel();
	public JList<String> sourceSelector = new JList<>();
	public JScrollPane sourceSelectorScroll = new JScrollPane();
	public JButton sourceSelectorCreateBtt = new JButton("Create");
	
	private EOCVSim eocvSim = null;
	
	private String title = "EasyOpenCV Simulator";
	private String titleMsg = "No pipeline";
	
	private String beforeTitle = "";
	private String beforeTitleMsg = "";
	
	public Visualizer(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}
	
	public void init() {
		
		rightContainer = new JPanel();
		
		/*
		* IMG VISUALIZER
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
		
		JLabel pipelineSelectorLabel = new JLabel("Select Pipeline");

		pipelineSelectorLabel.setFont(pipelineSelectorLabel.getFont().deriveFont(20.0f));
		
		pipelineSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
		pipelineSelectorContainer.add(pipelineSelectorLabel);
		
		JPanel pipelineSelectorScrollContainer = new JPanel();
		pipelineSelectorScrollContainer.setLayout(new GridLayout());
		pipelineSelectorScrollContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		
		pipelineSelectorScrollContainer.add(pipelineSelectorScroll);
		
		pipelineSelectorScroll.setViewportView(pipelineSelector);
		pipelineSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pipelineSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		pipelineSelectorContainer.add(pipelineSelectorScrollContainer);
		
		rightContainer.add(pipelineSelectorContainer);
	
		/*
		* SOURCE SELECTOR
		*/
		
		sourceSelectorContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		//sourceSelectorContainer.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JLabel sourceSelectorLabel = new JLabel("Select Source");

		sourceSelectorLabel.setFont(sourceSelectorLabel.getFont().deriveFont(20.0f));
		
		sourceSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
		
		sourceSelectorContainer.add(sourceSelectorLabel);
	
		JPanel sourceSelectorScrollContainer = new JPanel();
		sourceSelectorScrollContainer.setLayout(new GridLayout());
		sourceSelectorScrollContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		
		sourceSelectorScrollContainer.add(sourceSelectorScroll);
		
		sourceSelectorScroll.setViewportView(sourceSelector);
		sourceSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sourceSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		sourceSelectorContainer.add(sourceSelectorScrollContainer);
		sourceSelectorContainer.add(sourceSelectorCreateBtt);
		
		rightContainer.add(sourceSelectorContainer);
		
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
	    
	    frame.setVisible(true);
	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    asyncPleaseWaitDialog("Scanning for pipelines...", new Dimension(300, 150), true);
	    
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
	
	public JDialog pleaseWaitDialog(JDialog dialog, String message, Dimension size, boolean endAppOnCancel) {
	
		dialog.setModal(true);
		dialog.setLayout(new GridLayout(2, 1));
		
		dialog.setTitle("Hold on");
		
		JLabel msg = new JLabel(message);
		msg.setHorizontalAlignment(JLabel.CENTER);
		dialog.add(msg);
		
		JPanel exitBttPanel = new JPanel(new FlowLayout());
		JButton exitBtt = new JButton("Cancel");
		
		exitBttPanel.add(exitBtt);
		
		exitBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(endAppOnCancel) {
            		System.exit(0);
            	}
            }
        });

		dialog.add(exitBttPanel);
		
		if(size != null) {
			dialog.setSize(size);
		} else {
			dialog.setSize(new Dimension(400, 200));
		}

		dialog.setLocationRelativeTo(null);
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		dialog.setVisible(true);
		
		return dialog;
		
	}
	
	public RunnPleaseWaitDialog asyncPleaseWaitDialog(String message, Dimension size, boolean endAppOnCancel) {
		
		RunnPleaseWaitDialog rPWD = new RunnPleaseWaitDialog(message, size, endAppOnCancel);
		
		new Thread(rPWD).start();
		
		return rPWD;
		
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
	
	class RunnPleaseWaitDialog implements Runnable {

		String message = "";
		Dimension size = null;
		boolean endAppOnCancel = false;
		
		JDialog dialog = new JDialog(frame);
		
		public RunnPleaseWaitDialog(String message, Dimension size, boolean endAppOnCancel) {
			this.message = message;
			this.size = size;
			this.endAppOnCancel = endAppOnCancel;
		}
		
		@Override
		public void run() {
			
			pleaseWaitDialog(dialog, message, size, endAppOnCancel);
			
		}
		
	}
	
}