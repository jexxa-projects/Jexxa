package io.jexxa.utils.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExceptionAdapterFactory implements TypeAdapterFactory
{
    static void registerExceptionAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapterFactory(new ExceptionAdapterFactory());
    }


    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken)
    {
        // If the class that type token represents is a subclass of Base
        // then return your special adapter
        if(Exception.class.isAssignableFrom(typeToken.getRawType()))
        {
            Class<T> clazz = (Class<T>)typeToken.getRawType();

            return new ExceptionTypeAdapter<>(clazz);
        }
        return null;
    }

    public static class ExceptionTypeAdapter <T> extends TypeAdapter<T> {

        private final Class<T> rawType;

        ExceptionTypeAdapter(Class<T> rawType)
        {
            this.rawType = rawType;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            Exception exception = (Exception) value;
            out.beginObject()
                    .name("cause").value(String.valueOf(exception.getCause()))
                    .name("message").value(exception.getMessage())
                    .endObject();
        }

        @Override
        public T read(JsonReader reader) throws IOException {
            String cause = "";
            String message = "";

            // Read cause and message
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("cause".equals(name)) {
                    cause = reader.nextString();
                }

                if ("message".equals(name)) {
                    message = reader.nextString();
                }
            }
            reader.endObject();

            //Create exception
            Constructor<T> constructor;
            T exception;
            try {
                constructor = rawType.getConstructor(String.class, Throwable.class);
                exception = constructor.newInstance(message, new Throwable(cause));

            }  catch (NoSuchMethodException |  InvocationTargetException | IllegalAccessException | InstantiationException e) {
                return null;
            }

            return exception;
        }

    }
}
