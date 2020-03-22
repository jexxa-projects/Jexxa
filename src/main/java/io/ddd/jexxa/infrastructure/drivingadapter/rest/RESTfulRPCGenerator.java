package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * This class generates uniform IDs (URIs) for resources to be offered via REST
 *
 * This implementation uses following convention over configuration approach:
 *  * An object must have unique method names. Any method overloading is not supported
 *  * Methods from base class Object are ignored
 *  GET:
 *  * If a method has a return value != 'void' and no parameter then it is mapped to a GET method
 *  POST:
 *  * If a method has no return 'void' then it is mapped to a POST method
 *  * If a method has at least one parameter then it is mapped to a POST method
 */
class RESTfulRPCGenerator
{
    Object object;

    public RESTfulRPCGenerator(Object object)
    {
        this.object = object;
        validateUniqueURI();
    }

    
    public static class RESTfulRPC
    {
        enum HTTPCommand {GET, POST}
        
        private String resourcePath;
        private Method method;
        private HTTPCommand httpCommand;

        RESTfulRPC(HTTPCommand httpCommand, String resourcePath, Method method) {
            this.httpCommand = httpCommand;
            this.resourcePath = resourcePath;
            this.method = method;
        }

        String getResourcePath()
        {
            return resourcePath;
        }

        Method getMethod()
        {
            return method;
        }

        HTTPCommand getHTTPCommand()
        {
            return httpCommand;
        }
    }

    List<RESTfulRPC> getGETCommands() {
       var result = new ArrayList<RESTfulRPC>();

       List<Method> publicMethods = getPublicMethods(object.getClass());
       publicMethods
               .stream()
               .filter( element -> !(element.getReturnType().equals(void.class)) &&
                                     element.getParameterCount() == 0) // Convention for GET method
               .forEach( element -> result.add(
                       new RESTfulRPC(
                               RESTfulRPC.HTTPCommand.GET,
                               generateURI(element),
                               element)
                       ));

       return result;
    }


    List<RESTfulRPC> getPOSTCommands() {
        var result = new ArrayList<RESTfulRPC>();

        List<Method> publicMethods = getPublicMethods(object.getClass());
        publicMethods
                .stream()
                .filter( element -> (element.getReturnType().equals(void.class) ||
                                     element.getParameterCount() > 0)) // Convention for POST method
                .forEach( element -> result.add(
                        new RESTfulRPC(
                                RESTfulRPC.HTTPCommand.POST,
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
            throw new IllegalArgumentException("Mehtod names are not unique of Object " + object.getClass().getSimpleName());
        }
    }
}
