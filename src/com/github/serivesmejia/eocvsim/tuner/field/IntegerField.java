package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class IntegerField extends TunableField<Integer> {

    int value;
    int beforeValue;

    volatile boolean hasChanged = false;

    public IntegerField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS);
        value = (int)initialFieldValue;
    }

    @Override
    public void update() {

        hasChanged = value != beforeValue;

        if(hasChanged) {
            updateGuiFieldValues();
        }

        beforeValue = value;

    }

    @Override
    public void updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, value);
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

        try {
            value = Integer.parseInt(newValue);
            beforeValue = value;
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Parameter should be a valid numeric String");
        }

        setPipelineFieldValue(value);

    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return value;
    }

    @Override
    public boolean hasChanged() {
        return hasChanged;
    }

}
