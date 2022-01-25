package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.invocation.function.SerializableRunnable;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class LambdaUtils {
    @SuppressWarnings("java:S3011")
    static <T> SerializedLambda getSerializedLambda(SerializableRunnable function) {
        // get serialized lambda
        SerializedLambda serializedLambda = null;
        for (Class<?> clazz = function.getClass();
             clazz != null;
             clazz = clazz.getSuperclass())
        {
            try {
                Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                Object serialVersion = replaceMethod.invoke(function);

                // check if class is a lambda function
                if (serialVersion != null
                        && serialVersion.getClass() == SerializedLambda.class) {
                    serializedLambda = (SerializedLambda) serialVersion;
                    break;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // thrown if the method is not there. fall through the loop
            }
        }
        return serializedLambda;
    }
}
