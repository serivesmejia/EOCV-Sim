package com.github.serivesmejia.eocvsim.util;

import com.github.serivesmejia.eocvsim.util.extension.CvExt;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CvUtil {

    public static void matToBufferedImage(Mat m, BufferedImage buffImg) {
        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) buffImg.getRaster().getDataBuffer()).getData();
        m.get(0, 0, data);
    }

    public static BufferedImage matToBufferedImage(Mat m) {

        // Fastest code
        // output can be assigned either to a BufferedImage or to an Image
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        // Create an empty image in matching format
        BufferedImage buffImg = new BufferedImage(m.width(), m.height(), type);
        matToBufferedImage(m, buffImg);

        return buffImg;

    }

    public static BufferedImage Mat2BufferedImage(Mat mat) throws IOException {

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);

        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mob);

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(mob.toArray()));

        mob.release();

        return bi;

    }

    public static boolean checkImageValid(String imagePath) {

        try {

            //test if image is valid
            Mat img = Imgcodecs.imread(imagePath);

            if (img != null && !img.empty()) { //image is valid
                img.release();
                return true;
            } else { //image is not valid
                return false;
            }

        } catch (Throwable ex) {
            return false;
        }

    }

    public static boolean checkVideoValid(String videoPath) {

        try {

            VideoCapture capture = new VideoCapture();

            Mat img = new Mat();

            capture.open(videoPath);
            capture.read(img);
            capture.release();

            if (img != null && !img.empty()) { //image is valid
                img.release();
                return true;
            } else { //image is not valid
                if(img != null) img.release();
                return false;
            }

        } catch (Exception ex) {
            return false;
        }

    }


    public static Size getImageSize(String imagePath) {

        try {

            //test if image is valid
            Mat img = Imgcodecs.imread(imagePath);

            if (img != null && !img.empty()) { //image is valid
                Size size = img.size();
                img.release();
                return size;
            } else { //image is not valid
                return new Size(0, 0);
            }

        } catch (Throwable ex) {
            return new Size(0, 0);
        }

    }

    public static Size scaleToFit(Size currentSize, Size targetSize) {
        double targetAspectRatio = CvExt.aspectRatio(targetSize);
        double currentAspectRatio = CvExt.aspectRatio(currentSize);

        Log.info(currentSize + ", " + targetSize);
        Log.info(currentAspectRatio + ", " + targetAspectRatio);

        if(currentAspectRatio*100 == targetAspectRatio*100) {
            Log.info("a");
            return targetSize.clone();
        } else {

            double currentW = currentSize.width;
            double currentH = currentSize.height;

            double widthRatio = targetSize.width / currentW;
            double heightRatio = targetSize.height / currentH;
            double bestRatio = Math.max(widthRatio, heightRatio);

            return new Size(currentW * bestRatio, currentH * bestRatio);

        }
    }

}
