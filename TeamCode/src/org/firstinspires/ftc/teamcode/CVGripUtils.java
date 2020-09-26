package org.firstinspires.ftc.teamcode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Params;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.imgproc.Imgproc;

public class CVGripUtils {

    /**
     * Softens an image using a box filter.
     * @param input The image on which to perform the blur.
     * @param doubleRadius The radius for the blur.
     * @param output The image in which to store the output.
     */
    public static void cvBoxBlurMat(Mat input, double doubleRadius, Mat output) {
        int radius = (int)(doubleRadius + 0.5);
        int kernelSize = 2 * radius + 1;
        Imgproc.blur(input, output, new Size(kernelSize, kernelSize));
    }

    /**
     * Expands area of lower value in an image.
     * @param src the Image to erode.
     * @param kernel the kernel for erosion.
     * @param anchor the center of the kernel.
     * @param iterations the number of times to perform the erosion.
     * @param borderType pixel extrapolation method.
     * @param borderValue value to be used for a constant border.
     * @param dst Output Image.
     */
    public static void cvErode(Mat src, Mat kernel, Point anchor, double iterations,
                               int borderType, Scalar borderValue, Mat dst) {
        if (kernel == null) {
            kernel = new Mat();
        }
        if (anchor == null) {
            anchor = new Point(-1,-1);
        }
        if (borderValue == null) {
            borderValue = new Scalar(-1);
        }
        Imgproc.erode(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
    }

    /**
     * Expands area of higher value in an image.
     * @param src the Image to dilate.
     * @param kernel the kernel for dilation.
     * @param anchor the center of the kernel.
     * @param iterations the number of times to perform the dilation.
     * @param borderType pixel extrapolation method.
     * @param borderValue value to be used for a constant border.
     * @param dst Output Image.
     */
    public static void cvDilate(Mat src, Mat kernel, Point anchor, double iterations,
                                int borderType, Scalar borderValue, Mat dst) {
        if (kernel == null) {
            kernel = new Mat();
        }
        if (anchor == null) {
            anchor = new Point(-1,-1);
        }
        if (borderValue == null){
            borderValue = new Scalar(-1);
        }
        Imgproc.dilate(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
    }

    /**
     * Filters in an area of an image using a binary mask.
     * @param input The image on which the mask filters.
     * @param mask The binary image that is used to filter.
     * @param output The image in which to store the output.
     */
    public static void cvInvertedMask(Mat input, Mat mask, Mat output) {
        Mat cMask = mask.clone();
        mask.convertTo(cMask, CvType.CV_8UC1);
        Core.bitwise_not(cMask, cMask);
        Core.bitwise_xor(output, output, output);
        input.copyTo(output, cMask);
        cMask.release();
    }


    /**
     * Filter out an area of an image using a binary mask.
     * @param input The image on which the mask filters.
     * @param mask The binary image that is used to filter.
     * @param output The image in which to store the output.
     */
    public static void cvMask(Mat input, Mat mask, Mat output) {
        mask.convertTo(mask, CvType.CV_8UC1);
        Core.bitwise_xor(output, output, output);
        input.copyTo(output, mask);
    }

    /**
     * Detects groups of pixels in an image.
     * @param input The image on which to perform the find blobs.
     * @param blobDet The blob detector to be used (previously created)
     * @param blobList The output where the MatOfKeyPoint is stored.
     */
    public static void cvFindBlobs(Mat input, SimpleBlobDetector blobDet, MatOfKeyPoint blobList) {

        blobDet.detect(input, blobList);

    }

    public static SimpleBlobDetector cvCreateBlobDetector(double minArea, double[] circularity, boolean darkBlobs) {

        SimpleBlobDetector blobDet = SimpleBlobDetector.create();

        try {

            File tempFile = File.createTempFile("blob_cv_config", ".xml");

            StringBuilder config = new StringBuilder();

            config.append("<?xml version=\"1.0\"?>\n");
            config.append("<opencv_storage>\n");
            config.append("<thresholdStep>10.</thresholdStep>\n");
            config.append("<minThreshold>50.</minThreshold>\n");
            config.append("<maxThreshold>220.</maxThreshold>\n");
            config.append("<minRepeatability>2</minRepeatability>\n");
            config.append("<minDistBetweenBlobs>10.</minDistBetweenBlobs>\n");
            config.append("<filterByColor>1</filterByColor>\n");
            config.append("<blobColor>");
            config.append((darkBlobs ? 0 : 255));
            config.append("</blobColor>\n");
            config.append("<filterByArea>1</filterByArea>\n");
            config.append("<minArea>");
            config.append(minArea);
            config.append("</minArea>\n");
            config.append("<maxArea>");
            config.append(Integer.MAX_VALUE);
            config.append("</maxArea>\n");
            config.append("<filterByCircularity>1</filterByCircularity>\n");
            config.append("<minCircularity>");
            config.append(circularity[0]);
            config.append("</minCircularity>\n");
            config.append("<maxCircularity>");
            config.append(circularity[1]);
            config.append("</maxCircularity>\n");
            config.append("<filterByInertia>1</filterByInertia>\n");
            config.append("<minInertiaRatio>0.1</minInertiaRatio>\n");
            config.append("<maxInertiaRatio>" + Integer.MAX_VALUE + "</maxInertiaRatio>\n");
            config.append("<filterByConvexity>1</filterByConvexity>\n");
            config.append("<minConvexity>0.95</minConvexity>\n");
            config.append("<maxConvexity>" + Integer.MAX_VALUE + "</maxConvexity>\n");
            config.append("</opencv_storage>\n");

            FileWriter writer = new FileWriter(tempFile, false);
            writer.write(config.toString());
            writer.close();

            blobDet.read(tempFile.getPath());

            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return blobDet;

    }

    public static void cvFindContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) {

        Mat hierarchy = new Mat();
        contours.clear();

        int mode;
        if (externalOnly) {
            mode = Imgproc.RETR_EXTERNAL;
        } else {
            mode = Imgproc.RETR_LIST;
        }

        int method = Imgproc.CHAIN_APPROX_SIMPLE;
        Imgproc.findContours(input, contours, hierarchy, mode, method);

    }

}