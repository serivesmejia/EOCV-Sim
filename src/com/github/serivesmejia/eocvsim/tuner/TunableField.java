package com.github.serivesmejia.eocvsim.tuner;

import com.github.serivesmejia.eocvsim.EOCVSim;
import com.github.serivesmejia.eocvsim.gui.tuner.TunableFieldPanel;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;

public abstract class TunableField<T> {

    protected Field reflectionField;
    protected TunableFieldPanel fieldPanel;

    protected OpenCvPipeline pipeline;

    protected int guiFieldAmount = 1;
    protected AllowMode allowMode;

    protected EOCVSim eocvSim;

    protected Object initialFieldValue;

    private volatile boolean isFirstSet = true;

    public enum AllowMode { ONLY_NUMBERS, ONLY_NUMBERS_DECIMAL, TEXT }

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

        if(hasChanged()) { //execute if value is not the same to save resources

            reflectionField.set(pipeline, newValue);

            System.out.println("a");
            
            //do not pause if this is the first time we set the field value
            //for some reason, the change listener on textboxes is also
            //fired when we set the initial value programatically, not only
            //for user input.
            if(!isFirstSet) {
                eocvSim.pipelineManager.requestSetPaused(false);
            }

            isFirstSet = false;

        }

    }

    public abstract void setGuiFieldValue(int index, String newValue) throws IllegalAccessException;

    public final void setTunableFieldPanel(TunableFieldPanel fieldPanel) {
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

    public final AllowMode getAllowMode() {
        return allowMode;
    }

    public final boolean isOnlyNumbers() {
        return getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS ||
                getAllowMode() == TunableField.AllowMode.ONLY_NUMBERS_DECIMAL;
    }

    public abstract boolean hasChanged();

}
