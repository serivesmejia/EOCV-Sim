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
    protected AllowMode allowMode = AllowMode.TEXT;

    protected EOCVSim eocvSim;

    protected Object initialFieldValue;

    public enum AllowMode { ONLY_NUMBERS, ONLY_NUMBERS_DECIMAL, TEXT }

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim, AllowMode allowMode) throws IllegalAccessException {

        this.reflectionField = reflectionField;
        this.pipeline = instance;
        this.allowMode = allowMode;

        this.eocvSim = eocvSim;

        initialFieldValue = reflectionField.get(instance);

    }

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        this(instance, reflectionField, eocvSim, AllowMode.TEXT);
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

    public final AllowMode getAllowMode() {
        return allowMode;
    }

    public final boolean isOnlyNumbers() {
        return getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS ||
                getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL;
    }

}
