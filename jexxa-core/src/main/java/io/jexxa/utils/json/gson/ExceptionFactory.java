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
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

final class ExceptionFactory implements TypeAdapterFactory
{
    private static final String EXCEPTION_CONVENTION_WARNING = "Exception {} does not provide a recommended constructor such as {}(String message, Throwable cause)";


    static void registerExceptionAdapter(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapterFactory(new ExceptionFactory());
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
               return tryCreateException(message,cause)
                       .or(() -> tryCreateException(message))
                       .or(this::tryCreateException)
                       .orElseThrow(() -> new IOException("Invalid Exception: The expected exception "+ rawType.getSimpleName() + " does not provide a suitable constructor such as " + rawType.getSimpleName() + "(String message, Throwable cause)"));
        }

        private Optional<T> tryCreateException(String message, String cause)
        {
            try {
                return Optional.of(
                        rawType
                                .getConstructor(String.class, Throwable.class)
                                .newInstance(message, new Throwable(cause))
                );
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(EXCEPTION_CONVENTION_WARNING, rawType.getSimpleName(), rawType.getSimpleName());
                return Optional.empty();
            }
        }

        private Optional<T> tryCreateException(String message)
        {
            try {
                return Optional.of(
                        rawType
                                .getConstructor(String.class)
                                .newInstance(message)
                );
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(EXCEPTION_CONVENTION_WARNING, rawType.getSimpleName(), rawType.getSimpleName());
                return Optional.empty();
            }
        }

        private Optional<T> tryCreateException()
        {
            try {
                return Optional.of(
                        rawType
                                .getConstructor()
                                .newInstance()
                );
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                JexxaLogger.getLogger(ExceptionTypeAdapter.class).warn(EXCEPTION_CONVENTION_WARNING, rawType.getSimpleName(), rawType.getSimpleName());
                return Optional.empty();
            }
        }
    }
}
