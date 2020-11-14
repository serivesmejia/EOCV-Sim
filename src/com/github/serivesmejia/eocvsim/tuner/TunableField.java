package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField<T> {

    protected Field reflectionField;
    protected TunableFieldPanel fieldPanel;

    protected OpenCvPipeline pipeline;

    protected int guiFieldAmount = 1;
    protected boolean isOnlyNumbers = false;

    protected EOCVSim eocvSim;

    protected Object initialFieldValue;

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim, boolean isOnlyNumbers) throws IllegalAccessException {

        this.reflectionField = reflectionField;
        this.pipeline = instance;
        this.isOnlyNumbers = isOnlyNumbers;

        this.eocvSim = eocvSim;

        initialFieldValue = reflectionField.get(instance);

    }

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        this(instance, reflectionField, eocvSim, true);
    }

    public abstract void update();

    public abstract void updateGuiFieldValues();

    public void setPipelineFieldValue(T newValue) throws IllegalAccessException {
        reflectionField.set(pipeline, newValue);
        eocvSim.pipelineManager.requestSetPaused(false);
    }

    public abstract void setGuiFieldValue(int index, String newValue) throws IllegalAccessException;

    public void setTunableFieldPanel(TunableFieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
    }

    public abstract T getValue();

    public abstract Object getGuiFieldValue(int index);

    public final int getGuiFieldAmount() {
        return guiFieldAmount;
    }

    public final String getFieldName() {
        return reflectionField.getName();
    }

    public final boolean isOnlyNumbers() {
        return isOnlyNumbers;
    }

}
