package com.github.serivesmejia.eocvsim.tuner.field

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.tuner.TunableField
import com.github.serivesmejia.eocvsim.tuner.scanner.RegisterTunableField
import org.openftc.easyopencv.OpenCvPipeline
import java.lang.reflect.Field

@RegisterTunableField
class EnumField(private val instance: OpenCvPipeline,
                reflectionField: Field,
                eocvSim: EOCVSim) : TunableField<Enum<*>>(instance, reflectionField, eocvSim, AllowMode.TEXT) {

    val values = reflectionField.type.enumConstants

    private val initialValue = initialFieldValue as Enum<*>

    private var currentValue = initialValue
    private var beforeValue: Any? = null

    init {
        guiComboBoxAmount = 1
    }

    override fun init() {}

    override fun update() {
        if(hasChanged()) {
            currentValue = value
            updateGuiFieldValues()
        }
        beforeValue = currentValue
    }

    override fun updateGuiFieldValues() {
        fieldPanel.setFieldValue(0, currentValue.name)
    }

    override fun setGuiFieldValue(index: Int, newValue: String) {
        currentValue = java.lang.Enum.valueOf(initialValue::class.java, newValue)
        reflectionField.set(instance, currentValue)
    }

    override fun getValue() = currentValue

    override fun getGuiFieldValue(index: Int) = currentValue.name

    override fun getGuiComboBoxValues(index: Int): Array<out Any> {
        fieldPanel.setComboBoxSelection(index, currentValue)
        return values
    }

    override fun hasChanged() = reflectionField.get(instance) == beforeValue

}