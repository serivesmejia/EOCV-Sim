package com.github.serivesmejia.eocvsim.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;

public class CvUtil {

	 public static BufferedImage matToBufferedImage(Mat m) {

		 Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2BGR);

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

	public static Mat bufferedImageToMat(BufferedImage in) {
		Mat out;
		byte[] data;
		int r, g, b;

		if (in.getType() == BufferedImage.TYPE_INT_RGB) {
			out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
			data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
			for (int i = 0; i < dataBuff.length; i++) {
				data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
				data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
			}
		} else {
			out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
			data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
			for (int i = 0; i < dataBuff.length; i++) {
				r = (byte) ((dataBuff[i] >> 0) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 16) & 0xFF);
				data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
			}
		}
		out.put(0, 0, data);
		return out;
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
