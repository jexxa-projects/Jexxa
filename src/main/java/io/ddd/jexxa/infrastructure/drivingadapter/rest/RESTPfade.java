package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.ddd.jexxa.infrastructure.PortScanner;
import io.ddd.stereotype.applicationcore.ApplicationService;


public class RESTPfade
{
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

    HashMap<String, Method> pfade;
    List<String> keys = new ArrayList<>();

    private String path = this.getClass().getPackage().getName();
    String startPath = path.substring(0, path.indexOf("applicationservice.") + 6);
    String modulePath = path.substring(path.indexOf("applicationservice.") + 6, path.indexOf("infra"));

    public RESTPfade()
    {
        initRESTPfade();
    }
    

    public HashMap<String, Method> getMap()
    {
        return pfade;
    }


    private void initRESTPfade()
    {
        pfade = new HashMap<String, Method>();
        for (Method method : getRESTMethods())
        {

            System.out.println("add Methods");
            keys.add(buildRESTPath(method));
            pfade.put(buildRESTPath(method), method);
        } //todo : same method different parameters?
    }

    private String buildRESTPath(Method method)
    {
        //return "/" + modulePath.substring(0, modulePath.length() - 1) + "/" + method.getName(); //+ checkAndBuildForParameters(method);
        return "/" + method.getName(); //+ checkAndBuildForParameters(method);
    }

    private String checkAndBuildForParameters(Method method)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters)
        {
            if (parameter != null)
            {
                String parameterName = parameter.getType().getSimpleName();
                parameterName = Character.toLowerCase(parameterName.charAt(0)) + parameterName.substring(1);
                stringBuilder.append("/{" + parameterName + "}");
            }
        }
        return stringBuilder.toString();
    }


    private List<Method> getRESTMethods()
    {
        PortScanner portScanner = new PortScanner();
        List<Class<?>> classes = portScanner.findAnnotation(ApplicationService.class);

        List<Method> methods = new ArrayList<>();
        for (Class cls : classes)
        {
            System.out.println("HERE " + cls.getName());
            methods.addAll(Arrays.asList(cls.getMethods()));
        }
        methods.removeAll(Arrays.asList(Object.class.getMethods()));
        return methods;
    }

    static private String getRestURL(Method method) {
        return "/" + method.getDeclaringClass().getSimpleName() + "/" + method.getName();
    }

    static private List<Method> getPublicMethods(Class<?> clazz)
    {
        List<Method> result = new ArrayList<>(Arrays.asList(clazz.getMethods()));
        result.removeAll(Arrays.asList(Object.class.getMethods()));

        return result;
    }

    static public List<RestURL> getRestURLs(Class<?> clazz)
    {
        List<RestURL> result = new ArrayList<>();

        getPublicMethods(clazz)
                .forEach(element -> result.add(new RestURL(getRestURL(element), element)));
        return result;
    }
}
