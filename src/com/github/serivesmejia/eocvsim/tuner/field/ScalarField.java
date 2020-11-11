package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class ScalarField extends TunableField {

    int scalarSize;

    public ScalarField(OpenCvPipeline instance, Field reflectionField) {
        super(instance, reflectionField);
        scalarSize = ((Scalar)initialFieldValue).val.;
    }

    @Override
    public void updateValue(Object newValue) {

    }

}
