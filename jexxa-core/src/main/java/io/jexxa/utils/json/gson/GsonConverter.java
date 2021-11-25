package io.jexxa.utils.json.gson;

import java.io.Reader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import io.jexxa.utils.json.JSONConverter;

public class GsonConverter implements JSONConverter
{

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";
    private static final String NANO = "nano";

    private static final String YEARS = "years";
    private static final String MONTHS = "months";
    private static final String DAYS = "days";

    private static final String SECONDS = "seconds";
    private static final String NANOS = "nanos";
    private static final String INVALID_LOCAL_TIME_OBJECT = "GsonConverter: Invalid json representation of 'LocalTime'.";
    private static final String INVALID_LOCAL_DATE_OBJECT = "GsonConverter: Invalid json representation of 'LocalDate'.";
    private static final String INVALID_DURATION_OBJECT = "GsonConverter: Invalid json representation of 'Duration'.";
    private static final String INVALID_INSTANT_OBJECT = "GsonConverter: Invalid json representation of 'Instant'.";
    private static final String INVALID_PERIOD_OBJECT = "GsonConverter: Invalid json representation of 'Period'.";
    private static final String JSON_MESSAGE = "\nJSON message : ";


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
    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        gson = null; // reset internal gsonConverter so that it is recreated with new registered typeAdapter
    }

    public static void registerTypeAdapterFactory(TypeAdapterFactory typeAdapterFactory) {
        GSON_BUILDER.registerTypeAdapterFactory(typeAdapterFactory);
        gson = null; // reset internal gsonConverter so that it is recreated with new registered typeAdapter
    }

    public static Gson getGson()
    {
        if ( gson == null) {
            gson = GSON_BUILDER.create();
        }
        return gson;
    }

    public static void registerDateTimeAdapter(GsonBuilder gsonBuilder)
    {
        registerLocalDateAdapter(gsonBuilder);
        registerLocalDateAdapter(gsonBuilder);
        registerLocalDateTimeAdapter(gsonBuilder);
        registerZonedDateTimeAdapter(gsonBuilder);

        registerLocalTimeAdapter(gsonBuilder);
        registerDurationAdapter(gsonBuilder);
        registerInstantAdapter(gsonBuilder);
        registerPeriodAdapter(gsonBuilder);
    }

    private static GsonBuilder getGsonBuilder()
    {
        var gsonBuilder = new GsonBuilder();
        registerDateTimeAdapter(gsonBuilder);
        return gsonBuilder;
    }

    private static void registerLocalDateAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                    if (json.isJsonPrimitive())
                    {
                        return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                    }

                    Objects.requireNonNull(json.getAsJsonObject(), INVALID_LOCAL_DATE_OBJECT);
                    Objects.requireNonNull(json.getAsJsonObject().get(YEAR), INVALID_LOCAL_DATE_OBJECT);
                    Objects.requireNonNull(json.getAsJsonObject().get(MONTH), INVALID_LOCAL_DATE_OBJECT);
                    Objects.requireNonNull(json.getAsJsonObject().get(DAY), INVALID_LOCAL_DATE_OBJECT);
                    return LocalDate.of(
                            json.getAsJsonObject().get(YEAR).getAsInt(),
                            json.getAsJsonObject().get(MONTH).getAsInt(),
                            json.getAsJsonObject().get(DAY).getAsInt());
                });

        gsonBuilder.registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, typeOfSrc, serializationContext) -> new JsonPrimitive(src.toString()));

    }

    private static void registerLocalDateTimeAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(
                        json.getAsJsonPrimitive().getAsString()));

        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, serializationContext) -> new JsonPrimitive(src.toString()));

    }
    private static void registerZonedDateTimeAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) -> ZonedDateTime.parse(
                        json.getAsJsonPrimitive().getAsString()));

        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, serializationContext) ->
                        new JsonPrimitive(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(src.withZoneSameInstant(src.getZone()))));

    }

    private static void registerLocalTimeAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(LocalTime.class,
                (JsonSerializer<LocalTime>) (src, typeOfSrc, serializationContext) -> {
                    var jsonObject = new JsonObject();
                    jsonObject.add(HOUR, new JsonPrimitive(src.getHour()));
                    jsonObject.add(MINUTE, new JsonPrimitive(src.getMinute()));
                    jsonObject.add(SECOND, new JsonPrimitive(src.getSecond()));
                    jsonObject.add(NANO, new JsonPrimitive(src.getNano()));
                    return jsonObject;
                }
        );

        gsonBuilder.registerTypeAdapter(LocalTime.class,
                (JsonDeserializer<LocalTime>) (json, type, jsonDeserializationContext) ->
                {
                    Objects.requireNonNull(json.getAsJsonObject(), INVALID_LOCAL_TIME_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(HOUR), INVALID_LOCAL_TIME_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(MINUTE), INVALID_LOCAL_TIME_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(SECOND), INVALID_LOCAL_TIME_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(NANO), INVALID_LOCAL_TIME_OBJECT + getErrorMessage( json ));
                    return LocalTime.of(
                            json.getAsJsonObject().get(HOUR).getAsInt(),
                            json.getAsJsonObject().get(MINUTE).getAsInt(),
                            json.getAsJsonObject().get(SECOND).getAsInt(),
                            json.getAsJsonObject().get(NANO).getAsInt()
                    );
                });
    }

    private static void registerDurationAdapter(GsonBuilder gsonBuilder)
    {
        // Duration.class
        gsonBuilder.registerTypeAdapter(Duration.class,
        (JsonSerializer<Duration>) (src, typeOfSrc, serializationContext) -> {
            var jsonObject = new JsonObject();
            jsonObject.add(SECONDS, new JsonPrimitive( src.getSeconds() ));
            jsonObject.add(NANOS, new JsonPrimitive( src.getNano() ));
            return jsonObject;
        });

        gsonBuilder.registerTypeAdapter(Duration.class,
                (JsonDeserializer<Duration>) (json, type, jsonDeserializationContext) ->
                {
                    Objects.requireNonNull(json.getAsJsonObject(), INVALID_DURATION_OBJECT +  getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(SECONDS), INVALID_DURATION_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(NANOS), INVALID_DURATION_OBJECT +  getErrorMessage( json ));

                    return Duration.ofSeconds(
                            json.getAsJsonObject().get(SECONDS).getAsInt(),
                            json.getAsJsonObject().get(NANOS).getAsInt());
                }
        );
    }

    private static void registerInstantAdapter(GsonBuilder gsonBuilder)
    {
        // Instant.class
        gsonBuilder.registerTypeAdapter(Instant.class,
        (JsonSerializer<Instant>) (src, typeOfSrc, serializationContext) -> {
            var jsonObject = new JsonObject();
            jsonObject.add(SECONDS, new JsonPrimitive( src.getEpochSecond() ));
            jsonObject.add(NANOS, new JsonPrimitive( src.getNano() ));
            return jsonObject;
        });

        gsonBuilder.registerTypeAdapter(Instant.class,
                (JsonDeserializer<Instant>) (json, type, jsonDeserializationContext) ->
                {
                    Objects.requireNonNull(json.getAsJsonObject(), INVALID_INSTANT_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(SECONDS), INVALID_INSTANT_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(NANOS), INVALID_INSTANT_OBJECT +  getErrorMessage( json ));

                    return Instant.ofEpochSecond(
                            json.getAsJsonObject().get(SECONDS).getAsInt(),
                            json.getAsJsonObject().get(NANOS).getAsInt());
                }
        );
    }

    private static void registerPeriodAdapter(GsonBuilder gsonBuilder)
    {
        // Period.class
        gsonBuilder.registerTypeAdapter(Period.class,
                (JsonSerializer<Period>) (src, typeOfSrc, serializationContext) -> {
                    var jsonObject = new JsonObject();
                    jsonObject.add(YEARS, new JsonPrimitive(src.getYears()));
                    jsonObject.add(MONTHS, new JsonPrimitive(src.getMonths()));
                    jsonObject.add(DAYS, new JsonPrimitive(src.getDays()));
                    return jsonObject;
                });

        gsonBuilder.registerTypeAdapter(Period.class,
                (JsonDeserializer<Period>) (json, type, jsonDeserializationContext) ->
                {
                    Objects.requireNonNull(json.getAsJsonObject(), INVALID_PERIOD_OBJECT + getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(YEARS), INVALID_PERIOD_OBJECT +  getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(MONTHS), INVALID_PERIOD_OBJECT +  getErrorMessage( json ));
                    Objects.requireNonNull(json.getAsJsonObject().get(DAYS), INVALID_PERIOD_OBJECT +  getErrorMessage( json ));

                    return Period.of(
                            json.getAsJsonObject().get(YEARS).getAsInt(),
                            json.getAsJsonObject().get(MONTHS).getAsInt(),
                            json.getAsJsonObject().get(DAYS).getAsInt());
                }
        );
    }

    private static String getErrorMessage(JsonElement json)
    {
        return  JSON_MESSAGE + json;
    }
}

