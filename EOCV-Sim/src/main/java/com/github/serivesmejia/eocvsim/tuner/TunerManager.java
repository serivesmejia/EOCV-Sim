package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import com.github.serivesmejia.eocvsim.tuner.scanner.AnnotatedTunableFieldScanner;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.ReflectUtil;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TunerManager {

    private final EOCVSim eocvSim;

    private final List<TunableField> fields = new ArrayList<>();

    private static HashMap<Type, Class<? extends TunableField>> tunableFieldsTypes = null;

    private boolean firstInit = true;

    public TunerManager(EOCVSim eocvSim) {
        this.eocvSim = eocvSim;
    }

    public void init() {

        if(tunableFieldsTypes == null) {
            tunableFieldsTypes = new AnnotatedTunableFieldScanner(eocvSim.getParams().getScanForTunableFieldsIn())
                                .lookForTunableFields();
        }

        if (firstInit) {
            eocvSim.pipelineManager.onPipelineChange.doPersistent(this::reset);
            firstInit = false;
        }

        if (eocvSim.pipelineManager.getCurrentPipeline() != null) {
            addFieldsFrom(eocvSim.pipelineManager.getCurrentPipeline());
            eocvSim.visualizer.updateTunerFields(getTunableFieldPanels());
        }

    }

    public void update() {
        //update all fields
        for (TunableField field : fields) {
            try {
                field.update();
            } catch(Exception ex) {
                Log.error("Error while updating field " + field.getFieldName(), ex);
            }
        }
    }

    public void reset() {
        fields.clear();
        init();
    }

    public void addFieldsFrom(OpenCvPipeline pipeline) {

        if (pipeline == null) return;

        Field[] fields = pipeline.getClass().getFields();

        for (Field field : fields) {

            //we only accept non-final fields
            if (Modifier.isFinal(field.getModifiers())) continue;

            Class<?> type = field.getType();
            if(field.getType().isPrimitive()) { //wrap to java object equivalent if field type is primitive
                type = ReflectUtil.wrap(type);
            }

            //check if we have a registered TunableField which handles this type of field
            //if not, continue to next iteration
            if(!tunableFieldsTypes.containsKey(type)) continue;

            //yay we have a registered TunableField which handles this
            //now, lets do some more reflection to instantiate this TunableField
            //and add it to the list...
            try {

                Class<? extends TunableField> tunableFieldClass = tunableFieldsTypes.get(type);
                Constructor<? extends TunableField> constructor = tunableFieldClass.getConstructor(OpenCvPipeline.class, Field.class, EOCVSim.class);

                this.fields.add(constructor.newInstance(pipeline, field, eocvSim));

            } catch (Exception ex) {
                //oops rip
                Log.error("TunerManager", "Reflection error while processing field: " + field.getName(), ex);
            }

        }

    }

    public List<TunableFieldPanel> getTunableFieldPanels() {

        List<TunableFieldPanel> panels = new ArrayList<>();

        for (TunableField field : fields) {
            panels.add(new TunableFieldPanel(field, eocvSim));
        }

        return panels;

    }

}
