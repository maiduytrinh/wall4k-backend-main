package com.tp.wallios.entity.reference;

public enum DataType {
    IMAGE("image","0", 0),
    LIVE("live","1", 0),
    VIDEO("video","2", 0),
    DEPTH_EFFECT("depth_effect","3", 1);

    public final String value;
    public final String specialType;
    public final Integer contentType;

    private DataType(String value, String specialType, Integer contentType) {
        this.value = value;
        this.specialType = specialType;
        this.contentType = contentType;
    }
}
