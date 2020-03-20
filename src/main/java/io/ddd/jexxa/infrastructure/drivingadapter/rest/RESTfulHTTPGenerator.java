package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class generates uniform IDs (URIs) for resources to be offered via REST
 *
 * This implementation uses following convention over configuration approach:
 *  * Methods from base class Object are ignored 
 *  * If a method has no return value 'void' then it is mapped to a POST method
 *  * If a method has a return value != 'void' then it is mapped to a GET method 
 */
class RESTfulHTTPGenerator
{
    Object object;

    public RESTfulHTTPGenerator(Object object)
    {
        this.object = object;
    }

    
    public static class RESTfulHTTP
    {
        enum HTTPCommand {GET, PUT, POST, DELETE}
        
        private String restPath;
        private Method method;
        private HTTPCommand httpCommand;

        RESTfulHTTP(HTTPCommand httpCommand, String restURL, Method method) {
            this.httpCommand = httpCommand;
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

        HTTPCommand getHTTPCommand()
        {
            return httpCommand;
        }
    }

    List<RESTfulHTTP> getGETCommands() {
       var result = new ArrayList<RESTfulHTTP>();

       List<Method> publicMethods = getPublicMethods(object.getClass());
       publicMethods
               .stream()
               .filter( element -> !(element.getReturnType().equals(void.class)))
               .forEach( element2 -> result.add(
                       new RESTfulHTTP(
                               RESTfulHTTP.HTTPCommand.GET, // If return type != void => GET method
                               generateURI(element2),
                               element2)
                       ));

       return result;
    }


    List<RESTfulHTTP> getPOSTCommands() {
        var result = new ArrayList<RESTfulHTTP>();

        List<Method> publicMethods = getPublicMethods(object.getClass());
        publicMethods
                .stream()
                .filter( element -> element.getReturnType().equals(void.class)) // If return type == void => POST method
                .forEach( element -> result.add(
                        new RESTfulHTTP(
                                RESTfulHTTP.HTTPCommand.POST,
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


}
