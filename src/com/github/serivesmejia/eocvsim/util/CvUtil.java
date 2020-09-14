package com.github.serivesmejia.eocvsim.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class CvUtil {

	 public BufferedImage matToBufferedImage(Mat m) {
		 
		 // Fastest code
		 // output can be assigned either to a BufferedImage or to an Image
		 int type = BufferedImage.TYPE_BYTE_GRAY;
		 if ( m.channels() > 1 ) {
			 type = BufferedImage.TYPE_3BYTE_BGR;
		 }
		 
		 int bufferSize = m.channels()*m.cols()*m.rows();
		 byte [] b = new byte[bufferSize];
		 m.get(0,0,b); // get all the pixels
		 
		 BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		 final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		 System.arraycopy(b, 0, targetPixels, 0, b.length); 
		 
		 return image;
		 
	}
	
}
