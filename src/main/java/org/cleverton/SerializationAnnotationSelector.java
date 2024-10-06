package org.cleverton;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Optional;

public class SerializationAnnotationSelector {

    public static Optional<SerializationAnnotation> select(final Field field) {
        if (field.isAnnotationPresent(SerializedName.class)) {
           return Optional.of(new SerializedNameAnnotation(field));
        }
        else if (field.isAnnotationPresent(JsonProperty.class)) {
            return Optional.of(new JsonPropertyAnnotation(field));
        }

        return Optional.empty();
    }
}