package com.github.serivesmejia.eocvsim.input;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageSource implements InputSource {

	private String imgPath;
	private Size size;
	
	private Mat img;
	
	private boolean initialized = false;

	public ImageSource(String imgPath) {
		this(imgPath, null);
	}
	
	public ImageSource(String imgPath, Size size) {
		
		this.imgPath = imgPath;
		this.size = size;
		
	}
	
	@Override
	public void init() {
		
		if(initialized) return;
		
		initialized = true;
		
		readImage();
		
	}
	
	@Override
	public void reset() {
		
		if(!initialized) return;
		
		if(img != null) img.release();
		img = null;
		
		initialized = false;
		
	}
	
	public void readImage() {
		
		img = Imgcodecs.imread(this.imgPath);
				
		if(this.size != null) {
			
			Mat resImg = new Mat();
			Imgproc.resize(img, resImg, this.size, 0.0, 0.0, Imgproc.INTER_LINEAR);
			
			img.release();
			img = resImg;
			
		} else {
			this.size = img.size();
		}
		
	}

	@Override
	public Mat update() {
		return img;
	}

	@Override
	public String toString() {
		return "ImageSource(\"" + imgPath + "\", " + this.size.toString() + ")";
	}
	
}
