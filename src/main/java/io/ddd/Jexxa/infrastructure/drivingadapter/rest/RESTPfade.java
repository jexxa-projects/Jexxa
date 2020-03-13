package io.ddd.Jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.ddd.Jexxa.infrastructure.PortScanner;
import io.ddd.stereotype.applicationcore.ApplicationService;


public class RESTPfade
{
    HashMap<String, Method> pfade;
    List<String> keys = new ArrayList<>();

    private String path = this.getClass().getPackage().getName();
    String startPath = path.substring(0, path.indexOf("kanal.") + 6);
    String modulePath = path.substring(path.indexOf("kanal.") + 6, path.indexOf("infra"));

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
            keys.add(buildRESTPath(method));
            pfade.put(buildRESTPath(method), method);
        } //todo : same method different parameters?
    }

    private String buildRESTPath(Method method)
    {
        return "/" + modulePath.substring(0, modulePath.length() - 1) + "/" + method.getName(); //+ checkAndBuildForParameters(method);
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
            methods.addAll(Arrays.asList(cls.getMethods()));
        }
        methods.removeAll(Arrays.asList(Object.class.getMethods()));
        return methods;
    }
}
