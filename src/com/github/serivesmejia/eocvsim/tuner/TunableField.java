package com.github.serivesmejia.eocvsim.tuner;

import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField<T> {

    protected Field reflectionField;
    protected OpenCvPipeline pipeline;

    protected int guiFieldAmount = 1;

    protected Object initialFieldValue;

    public TunableField(OpenCvPipeline instance, Field reflectionField) throws IllegalAccessException {

        this.reflectionField = reflectionField;
        this.pipeline = instance;

        initialFieldValue = reflectionField.get(instance);

    }

    public void setPipelineFieldValue(T newValue) throws IllegalAccessException {
        reflectionField.set(pipeline, newValue);
    }

    public abstract void setGuiFieldValue(int index, Object newValue) throws IllegalAccessException;

    public abstract T getValue();

    public abstract Object getGuiFieldValue(int index);

    public final int getGuiFieldAmount() {
        return guiFieldAmount;
    }

    public final String getFieldName() {
        return reflectionField.getName();
    }

}
