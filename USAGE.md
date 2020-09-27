# Usage Explaination

The purpose of this software is to *simulate the package & class structure of OpenFTC's EasyOpenCV and a little bit of the FTC SDK*,
while also providing OpenCV functionality and a simple GUI. By simulating the aforementioned structure, it allows the imports, class names, 
to be the same as they would if you were using the FTC SDK with EasyOpenCV, allowing you to simply copy paste your vision code
onto your Android Studio project once you want to transfer it to a robot.<br/>

The pipeline classes **should be** placed under the **org.firstinspires.ftc.teamcode** package, in the **TeamCode** module. This way, they will be
automatically detected by the simulator and will be selectionable from the GUI.

<img src='images/eocvsim_screenshot_structure.png' width='301' height='183'><br/>

*(Also, the simulator already comes by default with some EasyOpenCV samples)*<br/>

The pipeline class **should also** extend the EOCV's OpenCvPipeline abstract class.<br/><br/>
Here's a quick, empty pipeline sample:

```java
package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public class SamplePipeline extends OpenCvPipeline {

    @Override
    public void init(Mat input) {
        /* Executed once, when the pipeline is selected */
    }

    @Override
    public Mat processFrame(Mat input) {
        /* Processing and detection stuff */
        return input; // Return the input mat
                      // (Or a new, processed mat)
    }

    @Override
    public void onViewportTapped() {
        /*
         * Executed everytime when the pipeline view is tapped/clicked.
         * This is executed from the UI thread, so whatever we do here
         * we must do it quickly.
         */
    }

}```
