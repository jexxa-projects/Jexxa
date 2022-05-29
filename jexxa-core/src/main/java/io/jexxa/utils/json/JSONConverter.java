package io.jexxa.utils.json;

import java.io.Reader;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public interface JSONConverter
{
    <T> T fromJson(String jsonString, Class<T> clazz);
    <T> T fromJson(String json, Type typeOfT);
    <T> T fromJson(Reader jsonStream, Class<T> clazz);
    <T> T fromJson(Reader jsonStream, Type typeOfT);

    <T> String toJson(T object);
}
