package com.github.serivesmejia.eocvsim.tuner.field.numeric;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.field.NumericField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class FloatField extends NumericField {

    protected float beforeValue;

    public FloatField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS_DECIMAL);
        value = (float) initialFieldValue;
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

        try {
            value = Float.parseFloat(newValue);
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Parameter should be a valid numeric String");
        }

        setPipelineFieldValue(value);

        beforeValue = value.floatValue();

    }

    @Override
    public boolean hasChanged() {
        boolean hasChanged = value.floatValue() != beforeValue;
        beforeValue = value.floatValue();
        return hasChanged;
    }

}
