package io.jexxa.adapterapi.invocation.context;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

class LambdaUtils {
    static  <T extends Serializable> Method getImplMethod(Object targetObject, T functionalInterface)
    {
        try {
            return targetObject
                    .getClass()
                    .getMethod(Objects.requireNonNull(getSerializedLambda(functionalInterface)).getImplMethodName());
        } catch (NoSuchMethodException | SecurityException e) {
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

    private LambdaUtils()
    {
        /* Private constructor */
    }
}
