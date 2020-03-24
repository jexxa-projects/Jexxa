package io.ddd.jexxa.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.locks.Condition;

public class ClassFactory
{

    public <T> T createByConstructor(Class<T> clazz)
    {
        var constructorList = new ArrayList<Constructor>( Arrays.asList(clazz.getDeclaredConstructors()));

        System.out.println("Constructor Size of " + clazz.getSimpleName() + " : " + constructorList.size());

        Object[] parameters = getParamters(constructorList.get(0));

//        System.out.println("Parameter Size " + parameters.length);

        try {
          return (T) constructorList.get(0).newInstance(parameters);
        } catch ( Exception e ) {
            System.out.println(e.getMessage());

            return null;
        }
    }
    

    Object[] getParamters(Constructor constructor) {
        if ( constructor.getParameterCount() == 0) {
            return null;
        }

        if ( constructor.getParameterCount() == 1) {
            Object[] parameterList = {new Integer(43)};
            return parameterList;
        }

        return null;
    }
}
