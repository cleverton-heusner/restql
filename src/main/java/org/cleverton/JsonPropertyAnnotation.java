package org.cleverton;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;

public class JsonPropertyAnnotation implements SerializationAnnotation {

    private final Field field;

    public JsonPropertyAnnotation(final Field field) {
        this.field = field;
    }

    @Override
    public String getValue() {
        return field.getAnnotation(JsonProperty.class).value();
    }
}