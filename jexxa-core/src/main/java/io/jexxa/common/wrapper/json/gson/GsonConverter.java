package io.jexxa.common.wrapper.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import io.jexxa.common.wrapper.json.JSONConverter;

import java.io.Reader;
import java.lang.reflect.Type;

import static io.jexxa.common.wrapper.json.gson.DateTimeAdapter.registerDateTimeAdapter;

@SuppressWarnings("unused")
public class GsonConverter implements JSONConverter
{
    private static Gson gson;
    private static final GsonBuilder GSON_BUILDER = getGsonBuilder();

    @Override
    public <T> T fromJson(String jsonString, Class<T> clazz)
    {
        return getGson().fromJson(jsonString, clazz);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT)
    {
           return getGson().fromJson(json, typeOfT);
    }

    @Override
    public <T> T fromJson(Reader jsonStream, Class<T> clazz) {
        return getGson().fromJson(jsonStream, clazz);
    }

    @Override
    public <T> T fromJson(Reader jsonStream, Type typeOfT) {
        return getGson().fromJson(jsonStream, typeOfT);
    }

    @Override
    public <T> String toJson(T object)
    {
        return getGson().toJson(object);
    }

    @SuppressWarnings("unused")
    public static void registerTypeAdapter(Type type, Object typeAdapter)
    {
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        gson = null; // reset internal gsonConverter so that it is recreated with new registered typeAdapter
    }

    public static void registerTypeAdapterFactory(TypeAdapterFactory typeAdapterFactory)
    {
        GSON_BUILDER.registerTypeAdapterFactory(typeAdapterFactory);
        gson = null; // reset internal gsonConverter so that it is recreated with new registered typeAdapter
    }

    public static synchronized Gson getGson()
    {
        if ( gson == null) {
            gson = GSON_BUILDER.create();
        }
        return gson;
    }

    private static GsonBuilder getGsonBuilder()
    {
        var gsonBuilder = new GsonBuilder();
        registerDateTimeAdapter(gsonBuilder);
        ExceptionFactory.registerExceptionAdapter(gsonBuilder);
        return gsonBuilder;
    }
}

