package com.github.serivesmejia.eocvsim.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
	
	private EOCVSim eocvSim = null;
	
	private String title = "EasyOpenCV Simulator";
	private String titleMsg = "No pipeline";
	
	private String beforeTitle = "";
	private String beforeTitleMsg = "";
	
	public Visualizer(EOCVSim eocvSim) {
		this.eocvSim = eocvSim;
	}
	
	public void init() {
		
		imgScrollContainer = new JPanel();
		imgScrollPane = new JScrollPane(imgScrollContainer);
		
		imgScrollContainer.add(img);
		
		imgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		imgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		frame.getContentPane().add(imgScrollPane);
		
		frame.setSize(640, 480);
		frame.setTitle("EasyOpenCV Simulator - No Pipeline");
	    
	    frame.setVisible(true);
	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
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
