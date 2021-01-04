package com.github.serivesmejia.eocvsim.pipeline

import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.ReflectUtil
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.openftc.easyopencv.OpenCvPipeline

@SuppressWarnings("unchecked")
class PipelineScanner(val scanInPackage: String = "org.firstinspires") {

    fun lookForPipelines(callback: (Class<OpenCvPipeline>) -> Unit) {

        Log.info("PipelineScanner", "Scanning for pipelines...")
        val scanResult = scanClasspath(scanInPackage)

        //iterate over the results of the scan
        for (routeClassInfo in scanResult.allClasses) {
            var foundClass: Class<*>

            foundClass = try {
                Class.forName(routeClassInfo.name)
            } catch (e1: ClassNotFoundException) {
                e1.printStackTrace()
                continue  //continue because we couldn't get the class...
            }

            if(ReflectUtil.hasSuperclass(foundClass, OpenCvPipeline::class.java)) {
                callback(foundClass as Class<OpenCvPipeline>);
            }

        }

    }

    fun scanClasspath(inPackage: String): ScanResult {
        //Scan for all classes in the specified package
        val classGraph = ClassGraph().enableAllInfo().acceptPackages(inPackage)
        return classGraph.scan()
    }

}