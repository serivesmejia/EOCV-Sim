package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public class NullPipeline extends OpenCvPipeline {

    private NullPipeline() { }

    @Override
    public Mat processFrame(Mat input) {
        return input;
    }

}
