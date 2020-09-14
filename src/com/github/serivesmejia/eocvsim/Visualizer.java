package com.github.serivesmejia.eocvsim;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class Visualizer {

	public JFrame frame = new JFrame();
	
	public Visualizer() {
		
	}
	
	public void init() {
		
		frame.setSize(640, 480);
	    frame.setLayout(new FlowLayout());
		
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	}
	
}
