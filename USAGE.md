# Usage Explaination

The purpose of this software is to *simulate the package & class structure of OpenFTC's EasyOpenCV and a little bit of the FTC SDK*,
while also providing OpenCV functionality and a simple GUI. By simulating the aforementioned structure, it allows the imports, class names, 
to be the same as they would if you were using the FTC SDK with EasyOpenCV, allowing you to simply copy paste your vision code
onto your Android Studio project once you want to transfer it to a robot.<br/>

The pipeline classes **should be** placed under the **org.firstinspires.ftc.teamcode** package, in the **TeamCode** module. This way, they will be
automatically detected by the simulator and will be selectionable from the GUI

<img src='images/eocvsim_screenshot_structure.png' width='301' height='183'><br/>

*(Also, the simulator already comes by default with some EasyOpenCV samples)*<br/>

The pipeline class **should also** extend the EOCV's OpenCvPipeline abstract class for it to be automatically detected.
