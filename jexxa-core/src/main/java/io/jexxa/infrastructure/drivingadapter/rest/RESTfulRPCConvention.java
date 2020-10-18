package io.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * This class generates uniform IDs (URIs) for resources to be offered via REST
 * using a convention over configuration approach:
 * <br>
 * Used conventions for URI:
 * <br>
 * {@code URI: http://<hostname>:<port>/<java simple class name>/<method name>}
 *  <br>
 * Example URI: http://localhost:7000/MyApplicationService/myMethod
 *  <br>
 *  This implies following conventions:
 *  <ul>
 *  <li> Simple name of a class must be unique within a single application </li>
 *  <li> Each class must have unique method names. Any method overloading is not supported </li>
 *  <li> Methods from base class `Object` are ignored </li>
 *  </ul>
 *  GET - mapping:
 *  <br>
 *  <ul>
 *  <li> If a method returns a type != 'void' and has no arguments then it is mapped to a GET method </li>
 *  </ul>
 * <br>
 *  POST - mapping:
 *  <ul>
 *  <li> In all other cases </li>
 *  </ul>
 */
class RESTfulRPCConvention
{
    private final Object object;

    RESTfulRPCConvention(Object object)
    {
        this.object = object;
        validateUniqueURI();
    }


    public static class RESTfulRPCMethod
    {
        enum HTTPCommand {GET, POST}

        private final String resourcePath;
        private final Method method;
        private final HTTPCommand httpCommand;

        RESTfulRPCMethod(HTTPCommand httpCommand, String resourcePath, Method method) {
            this.httpCommand = httpCommand;
            this.resourcePath = resourcePath;
            this.method = method;
        }

        protected String getResourcePath()
        {
            return resourcePath;
        }

        protected Method getMethod()
        {
            return method;
        }

        protected HTTPCommand getHTTPCommand()
        {
            return httpCommand;
        }
    }

    protected List<RESTfulRPCMethod> getGETCommands() {
       var result = new ArrayList<RESTfulRPCMethod>();

       List<Method> publicMethods = getPublicMethods(object.getClass());
       publicMethods
               .stream()
               .filter( element -> !(element.getReturnType().equals(void.class)) &&
                                     element.getParameterCount() == 0) // Convention for GET method
               .forEach( element -> result.add(
                       new RESTfulRPCMethod(
                               RESTfulRPCMethod.HTTPCommand.GET,
                               generateURI(element),
                               element)
                       ));

       return result;
    }


    protected List<RESTfulRPCMethod> getPOSTCommands() {
        var result = new ArrayList<RESTfulRPCMethod>();

        List<Method> publicMethods = getPublicMethods(object.getClass());
        publicMethods.stream()
                .filter( element -> (element.getReturnType().equals(void.class) ||
                                     element.getParameterCount() > 0)) // Convention for POST method
                .forEach( element -> result.add(
                        new RESTfulRPCMethod(
                                RESTfulRPCMethod.HTTPCommand.POST,
                                generateURI(element),
                                element)
                ));

        return result;
    }


    private String generateURI(Method method) {
        return "/" + method.getDeclaringClass().getSimpleName() + "/" + method.getName();
    }

    private List<Method> getPublicMethods(Class<?> clazz)
    {
        List<Method> result = new ArrayList<>(Arrays.asList(clazz.getMethods()));
        result.removeAll(Arrays.asList(Object.class.getMethods()));

        return result;
    }

    private void validateUniqueURI()
    {
        List<Method> publicMethods = getPublicMethods(object.getClass());
        List<String> methodNames = new ArrayList<>();

        publicMethods.forEach(element -> methodNames.add(generateURI(element)));

        // Make a unique list (by converting it into an HashSet) and compare its size with size of publicMethods.
        // If it is not equal URIs are not unique
        List<String> uniqueNames = new ArrayList<>( new HashSet<>(methodNames) );

        if (uniqueNames.size() != methodNames.size() ) {
            throw new IllegalArgumentException("Method names are not unique of Object " + object.getClass().getSimpleName());
        }
    }

    static RESTfulRPCConvention createRPCConvention( Object object)
    {
        return new RESTfulRPCConvention(object);
    }
}
