package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField<T> {

    protected Field reflectionField;
    protected TunableFieldPanel fieldPanel;

    protected OpenCvPipeline pipeline;
    protected AllowMode allowMode;
    protected EOCVSim eocvSim;
    protected Object initialFieldValue;
    private int guiFieldAmount = 1;
    private int guiComboBoxAmount = 0;

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim, AllowMode allowMode) throws IllegalAccessException {

        this.reflectionField = reflectionField;
        this.pipeline = instance;
        this.allowMode = allowMode;
        this.eocvSim = eocvSim;

        initialFieldValue = reflectionField.get(instance);

    }

    public TunableField(OpenCvPipeline instance, Field reflectionField, EOCVSim eocvSim) throws IllegalAccessException {
        this(instance, reflectionField, eocvSim, AllowMode.TEXT);
    }

    public abstract void update();

    public abstract void updateGuiFieldValues();

    public void setPipelineFieldValue(T newValue) throws IllegalAccessException {

        if (hasChanged()) { //execute if value is not the same to save resources
            reflectionField.set(pipeline, newValue);
        }

    }

    public abstract void setGuiFieldValue(int index, String newValue) throws IllegalAccessException;

    public void setGuiComboBoxValue(int index, String newValue) throws IllegalAccessException {
    }

    public final void setTunableFieldPanel(TunableFieldPanel fieldPanel) {
        this.fieldPanel = fieldPanel;
    }

    public abstract T getValue();

    public abstract Object getGuiFieldValue(int index);

    public Object[] getGuiComboBoxValues(int index) {
        return new Object[0];
    }

    public final int getGuiFieldAmount() {
        return guiFieldAmount;
    }

    public final void setGuiFieldAmount(int amount) {
        this.guiFieldAmount = amount;
    }

    public final int getGuiComboBoxAmount() {
        return guiComboBoxAmount;
    }

    public final void setGuiComboBoxAmount(int amount) {
        this.guiComboBoxAmount = amount;
    }

    public final String getFieldName() {
        return reflectionField.getName();
    }

    public final AllowMode getAllowMode() {
        return allowMode;
    }

    public final boolean isOnlyNumbers() {
        return getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS ||
                getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL;
    }

    public abstract boolean hasChanged();

    public enum AllowMode {ONLY_NUMBERS, ONLY_NUMBERS_DECIMAL, TEXT}

}
