package io.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * This class generates uniform IDs (URIs) for resources to be offered via REST
 *
 * This implementation uses following convention over configuration approach:
 *
 * URI: http://<specified hostname>:<specified port>/<java simple class name>/<method name></method>
 *  - Example URI: http://localhost:7000/MyApplicationService/myMethod
 *
 *  - This implies following conventions:
 *  - Simple name of a class must be unique within a single application
 *  - Each class must have unique method names. Any method overloading is not supported
 *  - Methods from base class `Object` are ignored
 *
 *  GET - mapping:
 *  - If a method returns a value != 'void' and has no arguments then it is mapped to a GET method
 *
 *  POST - mapping:
 *  - In all other cases 
 */
class RESTfulRPCConvention
{
    private final Object object;

    public RESTfulRPCConvention(Object object)
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
}
