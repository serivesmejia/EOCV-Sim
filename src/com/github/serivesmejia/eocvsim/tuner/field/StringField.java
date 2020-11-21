package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;
import java.util.Arrays;

public class StringField extends TunableField<String> {

    String value;

    String lastVal = "";

    volatile boolean hasChanged = false;

    public StringField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {

        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS_DECIMAL);

        String lastString = (String) initialFieldValue;

        value = new String(lastString);

    }

    @Override
    public void update() {

        hasChanged = !value.equals(lastVal);

        if(hasChanged) { //update values in GUI if they changed since last check
            updateGuiFieldValues();
        }

        lastVal = new String(value);

    }

    @Override
    public void updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, value);
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

        value = newValue;

        setPipelineFieldValue(value);

        lastVal = new String(value);

    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return value;
    }

    @Override
    public boolean hasChanged() {
        hasChanged = !value.equals(lastVal);
        return hasChanged;
    }

}
