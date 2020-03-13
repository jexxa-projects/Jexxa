package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class RESTPfade
{
    private RESTPfade()
    {

    }

    
    public static class RestURL {
        private String restPath;
        private Method method;

        RestURL(String restURL, Method method) {
            this.restPath = restURL;
            this.method = method;
        }

        String getRestURL()
        {
            return restPath;
        }

        Method getMethod()
        {
            return method;
        }
    }
    
    private static String getRestURL(Method method) {
        return "/" + method.getDeclaringClass().getSimpleName() + "/" + method.getName();
    }

    private static List<Method> getPublicMethods(Class<?> clazz)
    {
        List<Method> result = new ArrayList<>(Arrays.asList(clazz.getMethods()));
        result.removeAll(Arrays.asList(Object.class.getMethods()));

        return result;
    }

    public static List<RestURL> getRestURLs(Class<?> clazz)
    {
        List<RestURL> result = new ArrayList<>();

        getPublicMethods(clazz)
                .forEach(element -> result.add(new RestURL(getRestURL(element), element)));
        return result;
    }
}
