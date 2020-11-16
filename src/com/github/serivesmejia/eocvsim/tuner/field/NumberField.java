package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class NumberField extends TunableField<Number> {

    public NumberField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {

        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS);

        if(reflectionField.getType() != Integer.class && reflectionField.getType() != Long.class) {
            allowMode = AllowMode.ONLY_NUMBERS_DECIMAL;
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void updateGuiFieldValues() {

    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

    }

    @Override
    public Number getValue() {
        return 0;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return null;
    }

    @Override
    public boolean hasChanged() {
        return false;
    }

}
