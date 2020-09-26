package com.github.serivesmejia.eocvsim.input;

import com.google.gson.annotations.Expose;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageSource extends InputSource {

	@Expose
	private volatile String imgPath;
	@Expose
	private volatile Size size;
	
	private volatile Mat img;
	
	private volatile transient boolean initialized = false;

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

	@Override
	public void close() {

		if(img != null) img.release();
		img = null;

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
		return img.clone();
	}

	@Override
	public InputSource cloneSource() {
		return new ImageSource(imgPath, size);
	}

	@Override
	public String toString() {
		return "ImageSource(\"" + imgPath + "\", " + this.size.toString() + ")";
	}
	
}
