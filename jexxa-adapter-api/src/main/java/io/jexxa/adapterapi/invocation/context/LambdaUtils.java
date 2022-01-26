package io.jexxa.adapterapi.invocation.context;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class LambdaUtils {


    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
    static {
        WRAPPER_TYPE_MAP = new HashMap<>(16);
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
        WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    static  <T extends Serializable> Method getImplMethod(Object targetObject, T functionalInterface, Class<?>[] argTypes)
    {
        try {
            var serializedLambda = Objects.requireNonNull(getSerializedLambda(functionalInterface));

            return targetObject
                    .getClass()
                    .getMethod(serializedLambda.getImplMethodName(), argTypes);
        } catch (NoSuchMethodException e) { // Check if an alternative method with primitive types is available
            if (includePrimitives(argTypes))
            {
                return getImplMethod(targetObject, functionalInterface, convertToPrimitives(argTypes));
            }
            throw new IllegalArgumentException(e);
        } catch ( SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This method extracts the SerializedLambda from a functional interface. To ensure that this is available
     * the functional interface must implement Serializable which ensures that method `writeReplace` is automatically
     * generated
     *
     * @param functionalInterface from which SerializedLambda should be extracted
     * @param <T> Type of the functionalInterface
     * @return SerializedLambda of the functional interface
     */
    @SuppressWarnings("java:S3011")
    static <T extends Serializable> SerializedLambda getSerializedLambda(T functionalInterface) {
        SerializedLambda serializedLambda = null;
        for (Class<?> clazz = functionalInterface.getClass(); clazz != null; clazz = clazz.getSuperclass())
        {
            try {
                Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                Object serialVersion = replaceMethod.invoke(functionalInterface);

                // check if class is a lambda function
                if (serialVersion != null && serialVersion.getClass() == SerializedLambda.class) {
                    serializedLambda = (SerializedLambda) serialVersion;
                    break;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // thrown if the method is not there. fall through the loop
            }
        }
        return serializedLambda;
    }

    private static Class<?>[] convertToPrimitives(Class<?>[] types)
    {
        var result = new Class<?>[types.length];
        for (int i = 0; i< types.length; ++i)
        {
           result[i] = convertToPrimitive(types[i]);
        }

        return result;
    }

    private static Class<?> convertToPrimitive(Class<?> clazz)
    {
        if (WRAPPER_TYPE_MAP.containsKey(clazz))
        {
            return WRAPPER_TYPE_MAP.get(clazz);
        }

        return clazz;
    }

    private static boolean includePrimitives(Class<?>[] types)
    {
        for (Class<?> type : types) {
            if (WRAPPER_TYPE_MAP.containsKey(type)) {
                return true;
            }
        }
        return false;
    }


    private LambdaUtils()
    {
        /* Private constructor */
    }



}
