package com.github.serivesmejia.eocvsim.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

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
	
}
