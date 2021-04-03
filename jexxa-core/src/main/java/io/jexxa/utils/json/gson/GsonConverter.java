package io.jexxa.utils.json.gson;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import io.jexxa.utils.json.JSONConverter;

public class GsonConverter implements JSONConverter
{
    private static Gson gsonConverter;
    private static final GsonBuilder GSON_BUILDER = getGsonBuilder();

    @Override
    public <T> T fromJson(String jsonString, Class<T> clazz)
    {
        return getGson().fromJson(jsonString, clazz);
    }

    @Override
    public <T> String toJson(T object)
    {
        return getGson().toJson(object);
    }

    @SuppressWarnings("unused")
    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        gsonConverter = null; // reset internal gsonConverter so that it is recreated with new registered type
    }

    public static Gson getGson()
    {
        if ( gsonConverter == null) {
            gsonConverter = GSON_BUILDER.create();
        }
        return gsonConverter;
    }

    private static GsonBuilder getGsonBuilder()
    {
        var gsonBuilder = new GsonBuilder();
        registerDateTimeAdapter(gsonBuilder);
        return gsonBuilder;
    }

    private static void registerDateTimeAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                    if (json.isJsonPrimitive())
                    {
                        return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                    }
                    return LocalDate.of(json.getAsJsonObject().get("year").getAsInt(), json.getAsJsonObject().get("month").getAsInt(), json.getAsJsonObject().get("day").getAsInt());
                });

        gsonBuilder.registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, typeOfSrc, serializationContext) -> new JsonPrimitive(src.toString()));


        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString()));

        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, serializationContext) -> new JsonPrimitive(src.toString()));

        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) -> ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()));

        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, serializationContext) ->

                        new JsonPrimitive( DateTimeFormatter.ISO_OFFSET_DATE_TIME.
                                format(src.withZoneSameInstant(src.getZone())) ));

    }

}
