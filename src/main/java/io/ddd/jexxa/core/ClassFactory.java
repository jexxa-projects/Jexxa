package io.ddd.jexxa.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.locks.Condition;

public class ClassFactory
{

    public Object createByConstructor(Class<?> clazz)
    {
        var constructorList = new ArrayList<Constructor>( Arrays.asList(clazz.getDeclaredConstructors()));

        System.out.println("Constructor Size " + constructorList.size());

        Object[] parameters = getParamters(constructorList.get(0));
        try {
          return constructorList.get(0).newInstance(parameters);
        } catch ( Exception e ) {
          return null;
        }
    }
    

    Object[] getParamters(Constructor constructor) {
        Object[] parameterList = {new Integer(43)};
        return parameterList;
    }
}
