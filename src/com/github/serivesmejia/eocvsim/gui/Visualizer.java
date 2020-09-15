package com.github.serivesmejia.eocvsim.gui;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.Mat;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import com.github.serivesmejia.eocvsim.util.Log;

public class Visualizer {

	public JFrame frame = new JFrame();
	public volatile JLabel img = new JLabel();
	
	public JScrollPane imgScrollPane = null;
	public JPanel imgScrollContainer = null;
	public JPanel leftContainer = null;
	
	public GridBagLayout gridBagLayout = null;
	
	private EOCVSim eocvSim = null;
	
	private String title = "EasyOpenCV Simulator";
	private String titleMsg = "No pipeline";
	
	private String beforeTitle = "";
	private String beforeTitleMsg = "";
	
	public Visualizer(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}
	
	public void init() {
		
		gridBagLayout = new GridBagLayout();
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		imgScrollContainer = new JPanel();
		leftContainer = new JPanel();

		imgScrollPane = new JScrollPane(imgScrollContainer);
		
	    gbc = getGbc(0, 0, 4, 1, 0.75);
	    gridBagLayout.setConstraints(imgScrollPane, gbc);

	    gbc = getGbc(1, 0, 3, 1, 0.5);
	    gridBagLayout.setConstraints(leftContainer, gbc);
		
		imgScrollContainer.add(img);
		
		imgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		imgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		frame.setLayout(gridBagLayout);
		
		frame.getContentPane().add(imgScrollPane);
		frame.add(leftContainer);
		
		frame.setSize(640, 480);
		frame.setTitle("EasyOpenCV Simulator - No Pipeline");
	    
	    frame.setVisible(true);
	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	}
	
	private GridBagConstraints getGbc(int x, int y, int height, int width, double weightY) {
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = x;
	    gbc.gridy = y;
	    gbc.gridheight = height;
	    gbc.gridwidth = width;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 1.0;
	    gbc.weighty = weightY;

	    return gbc;
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
	
}
