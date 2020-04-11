package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


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
class RESTfulRPCModel
{
    final Object object;

    public RESTfulRPCModel(Object object)
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

    List<RESTfulRPCMethod> getGETCommands() {
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


    List<RESTfulRPCMethod> getPOSTCommands() {
        var result = new ArrayList<RESTfulRPCMethod>();

        List<Method> publicMethods = getPublicMethods(object.getClass());
        publicMethods
                .stream()
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

    Optional<RESTfulRPCMethod> getGETCommand(String methodEndsWith)
    {
        return  getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith(methodEndsWith)).
                findFirst();
    }

    Optional<RESTfulRPCMethod> getPOSTCommand(String methodEndsWith)
    {
        return  getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith(methodEndsWith)).
                findFirst();
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
}
