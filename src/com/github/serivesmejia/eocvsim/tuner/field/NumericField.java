package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class NumericField extends TunableField<Number> {

    protected Number value;

    protected volatile boolean hasChanged = false;

    public NumericField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim, AllowMode allowMode) throws IllegalAccessException {
        super(instance, reflectionField, eocvSim, allowMode);
    }

    @Override
    public void update() {

        if(value == null) return;

        try {
            value = (Number) reflectionField.get(pipeline);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        hasChanged = hasChanged();

        if(hasChanged) {
            updateGuiFieldValues();
        }

    }

    @Override
    public void updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, value);
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException { }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return value;
    }

    @Override
    public boolean hasChanged() {
        return false;
    }

}
