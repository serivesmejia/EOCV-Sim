package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField<T> {

    protected Field reflectionField;
    protected TunableFieldPanel fieldPanel;

    protected OpenCvPipeline pipeline;

    protected int guiFieldAmount = 1;
    protected boolean isOnlyNumbers = false;

    protected Object initialFieldValue;

    public TunableField(OpenCvPipeline instance, Field reflectionField, boolean isOnlyNumbers) throws IllegalAccessException {

        this.reflectionField = reflectionField;
        this.pipeline = instance;
        this.isOnlyNumbers = true;

        initialFieldValue = reflectionField.get(instance);

    }

    public TunableField(OpenCvPipeline instance, Field reflectionField) throws IllegalAccessException {
        this(instance, reflectionField, true);
    }

    public abstract void update();

    public abstract void updateGuiFieldValues();

    public void setPipelineFieldValue(T newValue) throws IllegalAccessException {
        reflectionField.set(pipeline, newValue);
    }

    public abstract void setGuiFieldValue(int index, Object newValue) throws IllegalAccessException;

    public void setTunableFieldPanel(TunableFieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
    }

    public abstract T getValue();

    public abstract Object getGuiFieldValue(int index);

    public final int getGuiFieldAmount() {
        return guiFieldAmount;
    }

    public final String getFieldName() {
        return reflectionField.getName();
    }

    public final boolean isOnlyNumbers() {
        return isOnlyNumbers;
    }

}
