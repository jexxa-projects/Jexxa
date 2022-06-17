package io.jexxa.utils.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.jexxa.utils.json.JSONManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

final class RecordFactory implements TypeAdapterFactory
{
    static void registerRecordFactory(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapterFactory(new RecordFactory());
        JSONManager.setJSONConverter(new GsonConverter());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
    {
        Class<T> clazz = (Class<T>) type.getRawType();
        if (!clazz.isRecord())
        {
            return null;
        }

        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new RecordTypeAdapter<>(delegate, clazz, gson);
    }

    public static class RecordTypeAdapter<T> extends TypeAdapter<T>
    {
        private final TypeAdapter<T> delegate;
        private final Class<T> rawType;
        private final Gson gson;

        public RecordTypeAdapter(TypeAdapter<T> delegate, Class<T> rawType, Gson gson)
        {
            this.delegate = delegate;
            this.rawType = rawType;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException
        {
            delegate.write(out, value);
        }

        @Override
        @SuppressWarnings("java:S3011")
        public T read(JsonReader reader) throws IOException
        {
            if (reader.peek() == JsonToken.NULL)
            {
                reader.nextNull();
                return null;
            }
            else
            {
                var recordComponents = rawType.getRecordComponents();
                var typeMap = new HashMap<String, Class<?>>();
                Arrays.stream(recordComponents)
                        .forEach(element -> typeMap.put(element.getName(), element.getType()));

                var argsMap = new HashMap<String, Object>();
                reader.beginObject();
                while (reader.hasNext())
                {
                    String name = reader.nextName();
                    if (typeMap.get(name) == null)
                    {
                        reader.skipValue();
                    }
                    else
                    {
                        argsMap.put(name, gson.getAdapter(typeMap.get(name)).read(reader));
                    }
                }
                reader.endObject();

                var argTypes = new Class<?>[recordComponents.length];
                var args = new Object[recordComponents.length];

                for (var i = 0; i < recordComponents.length; i++)
                {
                    argTypes[i] = recordComponents[i].getType();
                    args[i] = argsMap.get(recordComponents[i].getName());
                }
                Constructor<T> constructor;
                try
                {
                    constructor = rawType.getDeclaredConstructor(argTypes);
                    constructor.setAccessible(true);
                    return constructor.newInstance(args);
                }
                catch (NoSuchMethodException | InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }

}
