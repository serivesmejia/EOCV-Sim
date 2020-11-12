package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public class ScalarField extends TunableField<Scalar> {

    int scalarSize;
    Scalar scalar;

    public ScalarField(OpenCvPipeline instance, Field reflectionField) {

        super(instance, reflectionField);

        scalar = ((Scalar)initialFieldValue);
        scalarSize = scalar.val.length;

        guiFieldAmount = scalarSize;

    }

    @Override
    public void setGuiFieldValue(int index, Object newValue) throws IllegalAccessException {

        if(newValue instanceof Double) {
            scalar.val[index] = (double) newValue;
        } else {
            throw new IllegalArgumentException("Parameter should be a Double");
        }

        setPipelineFieldValue(scalar);

    }

    @Override
    public Scalar getValue() {
        return scalar;
    }

}
