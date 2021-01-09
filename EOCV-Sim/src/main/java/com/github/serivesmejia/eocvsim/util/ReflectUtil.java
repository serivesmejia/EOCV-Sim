package com.github.serivesmejia.eocvsim.util;

import com.github.serivesmejia.eocvsim.tuner.TunableField;

import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtil {

    public static boolean hasSuperclass(Class<?> clazz, Class<?> superClass) {
        try {
            clazz.asSubclass(superClass);
            return true;
        } catch (ClassCastException ex) {
            return false;
        }
    }

    public static Type[] getTypeArgumentsFrom(Class<?> clazz) {
        //get type argument
        Type sooper = clazz.getGenericSuperclass();
        return ((ParameterizedType)sooper).getActualTypeArguments();
    }

    public static <T> Class<T> wrap(Class<T> c) {
        return (Class<T>) MethodType.methodType(c).wrap().returnType();
    }

}
