package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
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

    private boolean firstInit = true;

    public TunerManager(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public void init() {

        if(firstInit) {
            eocvSim.pipelineManager.runOnChange(this::reset);
            firstInit = false;
        }

        if(eocvSim.pipelineManager.currentPipeline == null) return;

        addFieldsFrom(eocvSim.pipelineManager.currentPipeline);
        eocvSim.visualizer.updateTunerFields(getTunableFieldPanels());

    }

    public void update() {

    }

    public void reset() {
        fields.clear();
        init();
    }

    public void addFieldsFrom(OpenCvPipeline pipeline) {

        Field[] fields = pipeline.getClass().getFields();

        for(Field field : fields) {

            if(!field.canAccess(pipeline)) continue;

            TunableField toAddField = null; //for code simplicity

            try {
                if (field.getType() == Scalar.class) {
                    toAddField = new ScalarField(pipeline, field);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(toAddField != null) { this.fields.add(toAddField); }

        }

    }

    public List<TunableFieldPanel> getTunableFieldPanels() {

        List<TunableFieldPanel> panels = new ArrayList<>();

        for(TunableField field : fields) {
            panels.add(new TunableFieldPanel(field));
        }

        return panels;

    }

}
