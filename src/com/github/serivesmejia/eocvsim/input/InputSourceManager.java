package com.github.serivesmejia.eocvsim.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;

public class InputSourceManager {

	public volatile Mat lastMatFromSource = null;
	public volatile InputSource currInputSource = null;
	
	public HashMap<String, InputSource> sources = new HashMap<>();

	public String currInputSourceName = "";

	private volatile String nextInputSourceChange = "";

	public enum SourceType {
		IMAGE,
		WEBCAM,
		UNKNOWN
	}

	public void init() {

		Log.info("InputSourceManager", "Initializing...");
		
		Size size = new Size(580, 480);
		createDefaultImgInputSource("/ug_4.jpg", "ug_ocvsim_4.jpg", "Ultimate Goal 4 Ring", size);
		createDefaultImgInputSource("/ug_1.jpg", "ug_ocvsim_1.jpg", "Ultimate Goal 1 Ring", size);
		createDefaultImgInputSource("/ug_0.jpg", "ug_ocvsim_0.jpg", "Ultimate Goal 0 Ring", size);
		addInputSource("WebCam 1", new CameraSource(0));

		lastMatFromSource = new Mat();
		
	}
	
	private void createDefaultImgInputSource(String resourcePath, String fileName, String sourceName, Size imgSize) {
		try {
			InputStream is = InputSource.class.getResourceAsStream(resourcePath);
			File f = SysUtil.copyFileIsTemp(is, fileName, true).file;
			addInputSource(sourceName, new ImageSource(f.getAbsolutePath(), imgSize));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInputSourceNextFrame(String name) {
		this.nextInputSourceChange = name;
	}
	
	public void destroy() { }

	public void update() {

		if(nextInputSourceChange != "") {
			setInputSource(nextInputSourceChange);
			nextInputSourceChange = "";
		}

		if(currInputSource == null) return;
		lastMatFromSource = currInputSource.update();

	}

	public void addInputSource(String name, InputSource inputSource) {
		
		if(inputSource == null) {
			currInputSource = null;
			return;
		}
		
		sources.put(name, inputSource);
		
		Log.info("InputSourceManager", "Adding InputSource " + inputSource.toString() + " (" + inputSource.getClass().getSimpleName() + ")");
		
	}
	
	public void setInputSource(String sourceName) {

		if(currInputSource != null) {
			currInputSource.reset();
		}

		InputSource src = sources.get(sourceName);

		if(src != null) {
			src.reset();
		}

		src.init();
		
		currInputSource = src;
		
		Log.info("InputSourceManager", "Set InputSource to " + currInputSource.toString() + " (" + src.getClass().getSimpleName() + ")");
		
	}

	public SourceType getSourceType(String sourceName) {

		InputSource source = sources.get(sourceName);

		switch(source.getClass().getSimpleName()) {
			case "ImageSource":
				return SourceType.IMAGE;
			case "CameraSource":
				return SourceType.WEBCAM;
		}

		return SourceType.UNKNOWN;

	}

}
