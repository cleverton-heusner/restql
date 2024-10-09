package io.github.cleverton.heusner.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public class RestQlTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder
{
    public RestQlTypeResolverBuilder(final PolymorphicTypeValidator typeValidator)
    {
        super(ObjectMapper.DefaultTyping.NON_FINAL, typeValidator);
    }

    public StdTypeResolverBuilder init() {
        return super.init(JsonTypeInfo.Id.CLASS, null)
                .typeProperty("@class")
                .inclusion(JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public boolean useForType(final JavaType javaType)
    {
        return javaType.getRawClass().isAnnotationPresent(RestQl.class);
    }
}