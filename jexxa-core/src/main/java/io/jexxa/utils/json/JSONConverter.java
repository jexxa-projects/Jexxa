package io.jexxa.utils.json;

import java.lang.reflect.Type;

public interface JSONConverter
{
    <T> T fromJson(String jsonString, Class<T> clazz);
    <T> T fromJson(String json, Type typeOfT);
    <T> String toJson(T object);
}
