package com.github.serivesmejia.eocvsim.tuner.field;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.tuner.TunableField;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PointField extends TunableField<Point> {

    Point point;

    double[] lastXY = {0, 0};

    volatile boolean hasChanged = false;

    public PointField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {

        super(instance, reflectionField, eocvSim, AllowMode.ONLY_NUMBERS_DECIMAL);

        Point p = (Point) initialFieldValue;

        point = new Point(p.x, p.y);

        setGuiFieldAmount(2);

    }

    @Override
    public void update() {

        hasChanged = point.x != lastXY[0] || point.y != lastXY[1];

        if(hasChanged) { //update values in GUI if they changed since last check
            updateGuiFieldValues();
        }

        lastXY = new double[] { point.x, point.y };

    }

    @Override
    public void updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, point.x);
        fieldPanel.setFieldValue(1, point.y);
    }

    @Override
    public void setGuiFieldValue(int index, String newValue) throws IllegalAccessException {

       try {
           double value = Double.parseDouble(newValue);
           if(index == 0) {
               point.x = value;
           } else {
               point.y = value;
           }
       } catch(NumberFormatException ex) {
                throw new IllegalArgumentException("Parameter should be a valid numeric String");
       }

       setPipelineFieldValue(point);

       lastXY = new double[] {point.x, point.y};

    }

    @Override
    public Point getValue() {
        return point;
    }

    @Override
    public Object getGuiFieldValue(int index) {
        return index == 0 ? point.x : point.y;
    }

    @Override
    public boolean hasChanged() {
        hasChanged = point.x != lastXY[0] || point.y != lastXY[1];
        return hasChanged;
    }

}