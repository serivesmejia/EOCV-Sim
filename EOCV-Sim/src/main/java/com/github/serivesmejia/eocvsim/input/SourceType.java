package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.input.source.CameraSource;
import com.github.serivesmejia.eocvsim.input.source.ImageSource;
import com.github.serivesmejia.eocvsim.input.source.VideoSource;

public enum SourceType {

    IMAGE(ImageSource.class, "Image"),
    CAMERA(CameraSource.class, "Camera"),
    VIDEO(VideoSource.class, "Video"),
    UNKNOWN(null, "Unknown");

    public final Class<? extends InputSource> klazz;
    public final String coolName;

    SourceType(Class<? extends InputSource> klazz, String coolName) {
        this.klazz = klazz;
        this.coolName = coolName;
    }

    public static SourceType fromClass(Class<? extends InputSource> clazz) {
        for(SourceType sourceType : values()) {
            if(sourceType.klazz == clazz) {
                return sourceType;
            }
        }
        return UNKNOWN;
    }

    public static SourceType fromCoolName(String coolName) {
        for(SourceType sourceType : values()) {
            if(sourceType.coolName == coolName) {
                return sourceType;
            }
        }
        return UNKNOWN;
    }

}
