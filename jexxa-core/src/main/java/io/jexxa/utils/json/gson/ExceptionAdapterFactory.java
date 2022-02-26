package io.jexxa.utils.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.jexxa.utils.JexxaLogger;

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

            return createException(message, cause);
        }

        private T createException(String message, String cause) throws IOException {
            try {
                String exceptionConventionWarning = "Exception {} does not provide a recommended constructor such as {}(String message, Throwable cause)";

                try {
                    Constructor<T> constructor = rawType.getConstructor(String.class, Throwable.class);
                    return constructor.newInstance(message, new Throwable(cause));
                } catch (NoSuchMethodException e) {
                    JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(exceptionConventionWarning, rawType.getSimpleName(), rawType.getSimpleName());
                }

                try {
                    Constructor<T> constructor = rawType.getConstructor(String.class);
                    return constructor.newInstance(message);
                } catch (NoSuchMethodException e) {
                    JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(exceptionConventionWarning, rawType.getSimpleName(), rawType.getSimpleName());
                }

                try {
                    Constructor<T> constructor = rawType.getConstructor();
                    return constructor.newInstance();
                } catch (NoSuchMethodException e) {
                    JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(exceptionConventionWarning,  rawType.getSimpleName(), rawType.getSimpleName());
                }

                throw new IOException("Invalid Exception: The expected exception "+ rawType.getSimpleName() + " does not provide a suitable constructor such as " + rawType.getSimpleName() + "(String message, Throwable cause)");
            }

            catch ( InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new IOException("Invalid Exception: The exception " + rawType.getSimpleName() + " does not provide a suitable constructor such as " + rawType.getSimpleName() + "(String message, Throwable cause)");
            }
        }
    }
}
