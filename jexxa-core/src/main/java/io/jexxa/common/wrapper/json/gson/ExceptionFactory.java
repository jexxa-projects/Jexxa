package io.jexxa.common.wrapper.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;

final class ExceptionFactory implements TypeAdapterFactory
{
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
                } else if ("message".equals(name)) {
                    message = reader.nextString();
                } else {
                    getLogger(ExceptionTypeAdapter.class).warn("Unhandled element `{}` in exception {}", name, rawType.getSimpleName());
                    reader.skipValue();
                }
            }
            reader.endObject();

            return createException(message, cause);
        }

        private T createException(String message, String cause) throws IOException {
               return tryCreateException(message,cause)
                       .or(() -> tryCreateException(message))
                       .or(this::tryCreateException)
                       .orElseThrow(() -> new IOException("Invalid Exception: The expected exception "+ rawType.getSimpleName() + " does not provide a suitable constructor such as " +
                               rawType.getSimpleName() + "() or" +
                               rawType.getSimpleName() + "(String message) or" +
                               rawType.getSimpleName() + "(String message, Throwable cause)"));
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
                return Optional.empty();
            }
        }
    }
}
