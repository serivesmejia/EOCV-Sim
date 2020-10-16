package com.github.serivesmejia.eocvsim.pipeline;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class DefaultPipeline extends OpenCvPipeline {

    @Override
    public void init(Mat input) {
        telemetry.addData("[>]", "Default pipeline selected.");
        telemetry.update();
    }

    @Override
    public Mat processFrame(Mat input) {
        Mat matTxt = input.clone();
        // Outline
        Imgproc.putText (
                matTxt,
                "Default pipeline selected",
                new Point(0, 22),
                Imgproc.FONT_HERSHEY_PLAIN,
                2,
                new Scalar(255, 255, 255),
                5
        );
        //Text
        Imgproc.putText (
                matTxt,
                "Default pipeline selected",
                new Point(0, 22),
                Imgproc.FONT_HERSHEY_PLAIN,
                2,
                new Scalar(0, 0, 0),
                2
        );
        return matTxt;
    }
}
