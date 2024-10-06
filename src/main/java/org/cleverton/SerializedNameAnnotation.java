package org.cleverton;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

public class SerializedNameAnnotation implements SerializationAnnotation {

    private final Field field;

    public SerializedNameAnnotation(final Field field) {
        this.field = field;
    }

    @Override
    public String getValue() {
        return field.getAnnotation(SerializedName.class).value();
    }
}