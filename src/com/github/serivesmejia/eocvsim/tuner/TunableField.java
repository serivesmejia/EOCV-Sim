package com.github.serivesmejia.eocvsim.tuner;

import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField {

    protected Field reflectionField;
    protected Object initialFieldValue;

    public TunableField(OpenCvPipeline instance, Field reflectionField) {

        this.reflectionField = reflectionField;

        try {
            initialFieldValue = reflectionField.get(instance);
        } catch (IllegalAccessException e) { }

    }

    public abstract void updateValue(Object newValue);

}
