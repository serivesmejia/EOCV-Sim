package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.field.ScalarField;
import com.github.serivesmejia.eocvsim.util.Log;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TunerManager {

    private final EOCVSim eocvSim;

    private final List<TunableField> fields = new ArrayList<>();

    public TunerManager(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public void init() {
        eocvSim.pipelineManager.runOnChange(this::reset);
    }

    public void update() {

    }

    public void reset() {
        fields.clear();
        addFieldsFrom(eocvSim.pipelineManager.currentPipeline);
    }

    public void addFieldsFrom(OpenCvPipeline pipeline) {

        Field[] fields = pipeline.getClass().getFields();

        for(Field field : fields) {

            if(!field.canAccess(pipeline)) continue;

            TunableField toAddField = null; //for code simplicity

            if (field.getType() == Scalar.class) {
                toAddField = new ScalarField(pipeline, field);
            }

            if(toAddField != null) { this.fields.add(toAddField); }

        }

    }

}
