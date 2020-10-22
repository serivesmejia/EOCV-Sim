package com.github.serivesmejia.eocvsim.pipeline;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.openftc.easyopencv.OpenCvPipeline;

public class DefaultPipeline extends OpenCvPipeline {

    @Override
    public void init(Mat input) { }

    @Override
    public Mat processFrame(Mat input) {

        double aspectRatio = (double)input.height() / (double)input.width();

        double aspectRatioPercentage = aspectRatio / (580.0/480.0);

        telemetry.addData("[>]", "Default pipeline selected.");
        telemetry.update();

        Mat matTxt = input.clone();
        // Outline
        Imgproc.putText (
                matTxt,
                "Default pipeline selected",
                new Point(0, 22 * aspectRatioPercentage),
                Imgproc.FONT_HERSHEY_PLAIN,
                2 * aspectRatioPercentage,
                new Scalar(255, 255, 255),
                (int) Math.round(5 * aspectRatioPercentage)
        );

        //Text
        Imgproc.putText (
                matTxt,
                "Default pipeline selected",
                new Point(0, 22 * aspectRatioPercentage),
                Imgproc.FONT_HERSHEY_PLAIN,
                2 * aspectRatioPercentage,
                new Scalar(0, 0, 0),
                (int) Math.round(2 * aspectRatioPercentage)
        );

        return matTxt;

    }
}
