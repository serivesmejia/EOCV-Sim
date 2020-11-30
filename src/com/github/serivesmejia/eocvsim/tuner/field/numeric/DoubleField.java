package com.github.serivesmejia.eocvsim.tuner.field.numeric;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.field.NumericField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class DoubleField extends NumericField {

    private double beforeValue;

    public DoubleField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS_DECIMAL);
        value = (double) initialFieldValue;
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

        try {
            value = Double.valueOf(newValue);
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Parameter should be a valid numeric String");
        }

        setPipelineFieldValue(value);

        beforeValue = value.doubleValue();

    }

    @Override
    public boolean hasChanged() {
        boolean hasChanged = value.doubleValue() != beforeValue;
        beforeValue = value.doubleValue();
        return hasChanged;
    }


}
