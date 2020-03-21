package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

/**
 * Sie SRP der Klasse besteht in der Konvertierung von Entity Objekten in einen JSON String und vice versa
 */
public class JsonConverter
{
    private static final Gson GSON = createGson();

    private static Gson createGson()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
            final SimpleDateFormat[] zulaessigeDateForamte = new SimpleDateFormat[]{
                    new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            };

            ParseException parseException = null;
            for (SimpleDateFormat simpleDateFormat : zulaessigeDateForamte)
            {
                String date = jsonElement.getAsString();

                try
                {
                    return simpleDateFormat.parse(date);
                }
                catch (ParseException exp)
                {
                    parseException = exp;
                }
            }

            //noinspection ConstantConditions: parseException kann Null sein!
            if (parseException != null)
            {
                throw new JsonParseException(parseException);
            }
            return null;
        });
        return gsonBuilder.create();
    }
    private JsonConverter()
    {
    }

    /**
     * Erzeugt einen JSon String aus dem übergegebenen Obekt
     *
     * @param o Objekt das nach JSON umgewandelt werden soll
     * @return JSON-String
     */
    public static String toJson(Object o)
    {
        return GSON.toJson(o);
    }

    /**
     * Erezugt ein Objekt aus einem Json Repräsentation
     *
     * @param json     umzuwandelnder JSON-String
     * @param classOfT Klasse in die umgewandelt werden soll
     * @param <T>      Typ des Objektes
     * @return Objekt der Klasse
     */
    public static <T> T fromJson(String json, Class<T> classOfT)
    {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Erezugt ein Objekt aus einem Json Repräsentation
     *
     * @param jsonReader umzuwandelnder Json-String als Reader
     * @param typeOfT    Klasse in die umgewandelt werden soll
     * @return <T>        Typ des Objektes
     */
    public static <T> T fromJson(Reader jsonReader, Type typeOfT)
    {
        return GSON.fromJson(jsonReader, typeOfT);
    }

    /**
     * Erstellt eine Json Repräsentation für ein Objekt
     *
     * @param object   umzuwandelndes Objekt
     * @param jsonType Typ des umzuwandelnden Objekts
     * @param writer   Writer writer zur Erzeugung der Json Repräsentation
     */
    public static void toJson(Object object, Type jsonType, Writer writer)
    {
        GSON.toJson(object, jsonType, writer);
    }
}
