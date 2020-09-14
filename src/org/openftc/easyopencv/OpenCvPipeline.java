package org.openftc.easyopencv;

public abstract class OpenCvPipeline {

    public abstract Mat processFrame(Mat input);
    public void onViewportTapped() {}

    public void init(Mat mat) {}
	
}