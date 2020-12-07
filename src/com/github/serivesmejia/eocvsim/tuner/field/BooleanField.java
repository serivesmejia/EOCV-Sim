package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class BooleanField extends TunableField<Boolean> {

    boolean value;

    boolean lastVal;

    volatile boolean hasChanged = false;

    public BooleanField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {

        super(instance, reflectionField, eocvSim, AllowMode.TEXT);

        setGuiFieldAmount(0);
        setGuiComboBoxAmount(1);

        value = (boolean) initialFieldValue;

    }

    @Override
    public void update() {

        hasChanged = value != lastVal;

        if (hasChanged) { //update values in GUI if they changed since last check
            updateGuiFieldValues();
        }

        lastVal = value;

    }

    @Override
    public void updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, value);
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {
        setGuiComboBoxValue(index, newValue);
    }

    @Override
    public void setGuiComboBoxValue(int index, String newValue) throws IllegalAccessException {
        value = Boolean.parseBoolean(newValue);
        setPipelineFieldValue(value);
        lastVal = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return value;
    }

    @Override
    public Object[] getGuiComboBoxValues(int index) {
        return new Boolean[]{value, !value};
    }

    @Override
    public boolean hasChanged() {
        hasChanged = value != lastVal;
        return hasChanged;
    }

}