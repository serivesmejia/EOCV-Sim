package org.openftc.easyopencv;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.opencv.core.Mat;

public abstract class OpenCvPipeline {

    public Telemetry telemetry;

    public abstract Mat processFrame(Mat input);
    public void onViewportTapped() {}

    public void init(Mat mat) {}

}