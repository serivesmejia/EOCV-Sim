package com.github.serivesmejia.eocvsim.gui;

import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

import com.github.serivesmejia.eocvsim.util.CvUtil;

public class Visualizer {

	public JFrame frame = new JFrame();
	
	public JLabel img = new JLabel();
	
	public Visualizer() {
		
	}
	
	public void init() {
		
		frame.setSize(640, 480);
		frame.setTitle("EasyOpenCV Simulator - No Pipeline");
	    frame.setLayout(new FlowLayout());
		
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

	    frame.add(img);
	    
	}
	
	public void updateVisualizedMat(Mat mat) {
		
		ImageIcon icon = new ImageIcon(CvUtil.matToBufferedImage(mat));
		img.setIcon(icon);
		
	}
	
}
