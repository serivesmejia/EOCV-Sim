package com.github.serivesmejia.eocvsim.input;

import org.opencv.core.Mat;

import com.github.serivesmejia.eocvsim.util.Log;

public class InputSourceManager {

	public volatile Mat lastMatFromSource = null;
	public volatile InputSource currInputSource = null;
	
	private Thread inputSourceUpdater = new Thread(new InputSourceUpdater());
	
	public void init() {
		if(inputSourceUpdater.isAlive()) return;
		Log.info("InputSourceManager", "Starting InputSourceUpdater thread...");
		lastMatFromSource = new Mat();
		inputSourceUpdater.start();
	}
	
	public void destroy() {
		if(!inputSourceUpdater.isAlive()) return;
		inputSourceUpdater.interrupt();
	}
	
	public void setInputSource(InputSource inputSource) {
		
		if(inputSource == null) {
			currInputSource = null;
			return;
		}
		
		inputSource.init();
		currInputSource = inputSource;

		Log.info("InputSourceManager", "Changing InputSource to " + inputSource.toString());
		
	}
	
	class InputSourceUpdater implements Runnable {

		@Override
		public void run() {
			while(!Thread.interrupted()) {
				if(currInputSource == null) continue;
				lastMatFromSource = currInputSource.update();
			}
		}
		
	}
	
}
