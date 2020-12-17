package com.github.serivesmejia.eocvsim.pipeline;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class DefaultPipeline extends OpenCvPipeline {

    public int blur = 0;

    private Telemetry telemetry;

    public DefaultPipeline(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    public Mat processFrame(Mat input) {

        double aspectRatio = (double) input.height() / (double) input.width();
        double aspectRatioPercentage = aspectRatio / (580.0 / 480.0);

        telemetry.addData("[>]", "Default pipeline selected.");
        telemetry.addData("[Aspect Ratio]", aspectRatio + " (" + String.format("%.2f", aspectRatioPercentage * 100) + "%)");
        telemetry.addData("[Blur]", blur + " (change this value in tuner menu)");
        telemetry.update();

        if (blur > 0 && blur % 2 == 1) {
            Imgproc.GaussianBlur(input, input, new Size(blur, blur), 0);
        }

        // Outline
        Imgproc.putText(
                input,
                "Default pipeline selected",
                new Point(0, 22 * aspectRatioPercentage),
                Imgproc.FONT_HERSHEY_PLAIN,
                2 * aspectRatioPercentage,
                new Scalar(255, 255, 255),
                (int) Math.round(5 * aspectRatioPercentage)
        );


        //Text
        Imgproc.putText(
                input,
                "Default pipeline selected",
                new Point(0, 22 * aspectRatioPercentage),
                Imgproc.FONT_HERSHEY_PLAIN,
                2 * aspectRatioPercentage,
                new Scalar(0, 0, 0),
                (int) Math.round(2 * aspectRatioPercentage)
        );

        return input;

    }

}
