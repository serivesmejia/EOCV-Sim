<img src='src/resources/images/icon/ico_eocvsim_letters_transparent.png' height='128px' alt='EOCVSim'>

# Welcome!

EOCV-Sim (EasyOpenCV Simulator) is a straightforward way to test your pipelines in a 
simple user interface directly in your computer, simulating the EasyOpenCV library & 
FTC SDK structure, allowing you to simply copy paste directly your pipeline code once you want to 
transfer it onto your robot!

<img src='images/eocvsim_screenshot_1.png' width='718' height='580'>

# Compatibility

Because OpenCV in Java uses a native library, which are platform specific, the simulator is currently limited to the following platforms:

* Windows x64 (tested)
* Windows x32 (untested)
* MacOS x64 (tested)*
* Linux x64 (tested for Ubuntu 20.04)<br/>

*\*For Mac support, you will also need to follow [these steps](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html#install-opencv-3-x-under-macos) to download some other required native libraries, except for the last step since EOCV Sim already loads automatically the core dylib file.*

# Installation

No complicated setup is required, straight up importing the project into IntelliJ IDEA:

1) **Download & install the Java Development Kit if you haven't already:**<br/><br/>
      JDK 13 is the most tested with the Sim, but any JDK > 8 will probably work fine.<br/>
      You can download JDK > 8 from [the Oracle webpage](https://www.oracle.com/java/technologies/javase-downloads.html)<br/>
      **Since JDK 13 seems unavailable on the Oracle site, you can alternatively use JDK 14 or 11**<br/><br/>
      Here is a [step by step video](https://www.youtube.com/watch?v=IJ-PJbvJBGs) of the JDK installation process<br/>

2) **Download & install IntelliJ IDEA Community IDE if you haven't already:**<br/><br/>
      You can download it from the JetBrains webpage (https://www.jetbrains.com/idea/download/)<br/>
      Here is another great [step by step video](https://www.youtube.com/watch?v=E2okEJIbUYs) for IntelliJ installation.
     
3) **Clone and import the project:**<br/>

      1) Open IntelliJ IDEA and in the main screen click on "Get from Version Control"<br/>
      
            <img src='images/eocvsim_screenshot_installation_1.png' width='399' height='249'><br/><br/>
         Alternatively, if you already had another project opened, go to File > New > Project from Version Control...<br/><br/>
            <img src='images/eocvsim_screenshot_installation_2.png' width='419' height='76'>
            
      2) Another window will show up for cloning and importing a repository into IntelliJ<br/>
      
         1) In the "URL" field, enter: ```https://github.com/serivesmejia/EOCV-Sim.git```<br/>
         2) The directory can be changed, but it will be automatically filled so it's not necessary.
         3) Make sure the "Version control" is set to "Git".<br/><br/>
         <img src='images/eocvsim_screenshot_installation_3.png' width='608' height='363'><br/>
         4) After that, click on the "Clone" button, located at the bottom right and the cloning process will begin...<br/>    
         <img src='images/eocvsim_screenshot_installation_4.png' width='407' height='83'><br/>
         5) After the cloning finishes, the project should automatically import and you'll have something like this:<br/><br/>
            <img src='images/eocvsim_screenshot_installation_5.png' width='500' height='267'><br/>
            
4) **Change to packages view** (optional):<br/><br/>
      In order to have a better look of your project's sources, I recommend to change the view to "package" as explained next:<br/>
      1) Go to the left where your project files are shown, and click on the "Project" drop down list
      2) Select "Packages" from the list and the view will change.<br/><br/>
      Here's a quick gif illustrating these steps:<br/><br/>
      <img src='images/eocv_installation_changeview.gif' width='512' height='288'><br/><br/>
### And you're ready to go! Refer to the [usage explanation](https://github.com/serivesmejia/EOCV-Sim/blob/master/USAGE.md) for further details on how to utilize the simulator.<br/>

# Contact
For any quick troubleshooting or help, you can find me on Discord as *serivesmejia#8237* and on the FTC discord server. I'll be happy to assist you in any issue you might have :)<br/><br/>
For bug reporting or feature requesting, use the [issues tab](https://github.com/serivesmejia/EOCV-Sim/issues) in this repository.
