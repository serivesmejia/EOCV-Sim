package com.github.serivesmejia.eocvsim.tuner.field.numeric;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.field.NumericField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class LongField extends NumericField {

    private long beforeValue;

    public LongField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS);
        value = (long) initialFieldValue;
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

        try {
            value = Long.valueOf(newValue);
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Parameter should be a valid numeric String");
        }

        setPipelineFieldValue(value);

        beforeValue = value.longValue();

    }

    @Override
    public boolean hasChanged() {
        boolean hasChanged = value.longValue() != beforeValue;
        beforeValue = value.longValue();
        return hasChanged;
    }

}
