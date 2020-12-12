package com.github.serivesmejia.eocvsim.pipeline;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.util.Log;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.openftc.easyopencv.OpenCvPipeline;

public class PipelineScanner {

    PipelineManager pipelineManager;

    public PipelineScanner(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    @SuppressWarnings("unchecked")
    public void lookForPipelines(Visualizer.AsyncPleaseWaitDialog lookForPipelineAPWD) {

        Log.info("PipelineScanner", "Scanning for pipelines...");

        ScanResult scanResult = scanClasspath("org.firstinspires");

        //iterate over the results of the scan
        for (ClassInfo routeClassInfo : scanResult.getAllClasses()) {

            Class<?> foundClass;

            try {
                foundClass = Class.forName(routeClassInfo.getName());
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
                continue; //continue because we couldn't get the class...
            }

            //Scan recursively until we find a OpenCvPipeline superclass or we hit the Object superclass
            Class<?> superClass = foundClass.getSuperclass();

            while (superClass != null) {

                if (superClass == OpenCvPipeline.class) { //Yay we found a pipeline

                    Log.info("PipelineScanner", "Found pipeline " + routeClassInfo.getName());
                    if (lookForPipelineAPWD != null)
                        lookForPipelineAPWD.subMsg.setText("Found pipeline " + routeClassInfo.getSimpleName());

                    pipelineManager.addPipelineClass(foundClass);
                    break;

                }

                //Didn't found a pipeline, continue searching...
                superClass = superClass.getSuperclass();

            }

        }

    }

    public ScanResult scanClasspath(String inPackage) {
        //Scan for all classes in the specified package
        ClassGraph classGraph = new ClassGraph().enableAllInfo().acceptPackages(inPackage);
        return classGraph.scan();
    }

}