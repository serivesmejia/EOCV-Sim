package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ScalarField extends TunableField<Scalar> {

    int scalarSize;
    Scalar scalar;

    double[] lastVal = {};

    public ScalarField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {

        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS_DECIMAL);

        Scalar lastScalar = (Scalar) initialFieldValue;

        scalar = new Scalar(lastScalar.val);
        scalarSize = scalar.val.length;

        guiFieldAmount = scalarSize;

    }

    @Override
    public void update() {
        if(!Arrays.equals(scalar.val, lastVal)) { //update values in GUI if they changed since last check
            updateGuiFieldValues();
        }
        lastVal = scalar.val;
    }

    @Override
    public void updateGuiFieldValues() {
        for(int i = 0 ; i < scalar.val.length ; i++) {
            fieldPanel.setFieldValue(i, scalar.val[i]);
        }
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

       try {
           scalar.val[index] = Double.parseDouble(newValue);
        } catch(NumberFormatException ex) {
                throw new IllegalArgumentException("Parameter should be a valid numeric String");
        }

        setPipelineFieldValue(scalar);

    }

    @Override
    public Scalar getValue() {
        return scalar;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return scalar.val[index];
    }

}
