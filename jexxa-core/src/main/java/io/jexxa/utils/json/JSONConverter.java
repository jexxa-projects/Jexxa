package io.jexxa.utils.json;

public interface JSONConverter
{
    <T> T fromJson(String jsonString, Class<T> clazz);
    <T> String toJson(T object);
}
