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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Gson support for Java record types.
 * Taken from <a href="https://gist.github.com/knightzmc/cf26d9931d32c78c5d777cc719658639">Here</a> and adapted to framework
 */
public class RecordFactory implements TypeAdapterFactory {

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = new HashMap<>();

    static {
        PRIMITIVE_DEFAULTS.put(byte.class, (byte) 0);
        PRIMITIVE_DEFAULTS.put(int.class, 0);
        PRIMITIVE_DEFAULTS.put(long.class, 0L);
        PRIMITIVE_DEFAULTS.put(short.class, (short) 0);
        PRIMITIVE_DEFAULTS.put(double.class, 0D);
        PRIMITIVE_DEFAULTS.put(float.class, 0F);
        PRIMITIVE_DEFAULTS.put(char.class, '\0');
        PRIMITIVE_DEFAULTS.put(boolean.class, false);
    }

    static void registerRecordFactory(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapterFactory(new RecordFactory());
        JSONManager.setJSONConverter(new GsonConverter());
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
    {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) type.getRawType();
        if (!clazz.isRecord())
        {
            return null;
        }
        return new RecordTypeAdapter<>(gson.getDelegateAdapter(this, type), clazz, gson);
    }

    public static class RecordTypeAdapter<T> extends TypeAdapter<T>
    {

        private final TypeAdapter<T> delegate;
        private final Class<T> clazz;
        private final Gson gson;
        RecordTypeAdapter(TypeAdapter<T> delegate, Class<T> clazz, Gson gson)
        {
            this.delegate = delegate;
            this.clazz = clazz;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter jsonWriter, T value) throws IOException
        {
            delegate.write(jsonWriter, value);
        }

        @Override
        @SuppressWarnings("java:S3011")
        public T read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            var recordComponents = clazz.getRecordComponents();
            var typeMap = getTypeMap(recordComponents);
            var argsMap = getArgsMap(typeMap, reader);
            var argTypes = getArgTypes(recordComponents);
            var args = getArgs(recordComponents, argsMap, typeMap);

            return createInstance(args, argTypes);
        }

        @SuppressWarnings("java:S3011")
        private T createInstance(Object[] args, Class<?>[] argTypes)
        {
            try {
                var constructor = clazz.getDeclaredConstructor(argTypes);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            } catch (NoSuchMethodException | InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @SuppressWarnings("java:S1452")
        private Map<String, TypeToken<?>> getTypeMap(RecordComponent[] recordComponents)
        {
            Map<String, TypeToken<?>> typeMap = new HashMap<>();
            Arrays.stream(recordComponents)
                    .forEach(element -> typeMap.put(element.getName(), TypeToken.get(element.getGenericType())));
            return typeMap;
        }

        private Object[] getArgs(RecordComponent[] recordComponents, Map<String, Object> argsMap, Map<String, TypeToken<?>>typeMap)
        {
            var args = new Object[recordComponents.length];
            for (int i = 0; i < recordComponents.length; i++) {
                var name = recordComponents[i].getName();
                var value = argsMap.get(name);
                var type = typeMap.get(name);

                if (value == null && (type != null && type.getRawType().isPrimitive())) {
                    value = PRIMITIVE_DEFAULTS.get(type.getRawType());
                }
                args[i] = value;
            }
            return args;
        }

        private Class<?>[] getArgTypes(RecordComponent[] recordComponents)
        {
            var argTypes = new Class<?>[recordComponents.length];
            for (int i = 0; i < recordComponents.length; i++) {
                argTypes[i] = recordComponents[i].getType();
            }
            return argTypes;
        }

        private Map<String, Object> getArgsMap(Map<String, TypeToken<?>> typeMap, JsonReader reader ) throws IOException {
            var argsMap = new HashMap<String, Object>();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                var type = typeMap.get(name);
                if (type != null) {
                    argsMap.put(name, gson.getAdapter(type).read(reader));
                } else {
                    gson.getAdapter(Object.class).read(reader);
                }
            }
            reader.endObject();
            return argsMap;
        }
    }
}