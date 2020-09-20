package com.github.serivesmejia.eocvsim.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

public class CvUtil {

	 public static BufferedImage matToBufferedImage(Mat m) {
		 
		 // Fastest code
		 // output can be assigned either to a BufferedImage or to an Image
		 int type = BufferedImage.TYPE_BYTE_GRAY;
		 if ( m.channels() > 1 ) {
			 type = BufferedImage.TYPE_3BYTE_BGR;
		 }
		 
		 // Create an empty image in matching format
		 BufferedImage buffImg = new BufferedImage(m.width(), m.height(), type);

		 // Get the BufferedImage's backing array and copy the pixels directly into it
		 byte[] data = ((DataBufferByte) buffImg.getRaster().getDataBuffer()).getData();
		 m.get(0, 0, data);
		 
		 return buffImg;
		 
	}

	public static boolean checkImageValid(String imagePath) {

		try {

			//test if image is valid
			Mat img = Imgcodecs.imread(imagePath);

			if(img != null && !img.empty()) { //image is valid
				img.release();
				return true;
			} else { //image is not valid
				return false;
			}

		} catch(Throwable ex) {
			return false;
		}

	}

	public static Size getImageSize(String imagePath) {

		try {

			//test if image is valid
			Mat img = Imgcodecs.imread(imagePath);

			if(img != null && !img.empty()) { //image is valid
				Size size = img.size();
				img.release();
				return size;
			} else { //image is not valid
				return new Size(0, 0);
			}

		} catch(Throwable ex) {
			return new Size(0, 0);
		}

	}
	
}
