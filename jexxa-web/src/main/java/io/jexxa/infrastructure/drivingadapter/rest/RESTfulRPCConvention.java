package io.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
 * Example URI: <a href="http://localhost:7500/MyApplicationService/myMethod">http://localhost:7500/MyApplicationService/myMethod</a>
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
@SuppressWarnings("ClassCanBeRecord")
class RESTfulRPCConvention
{
    private final Object object;

    RESTfulRPCConvention(Object object)
    {
        this.object = object;
        validateUniqueURI();
    }


    record RESTfulRPCMethod(RESTfulRPCConvention.RESTfulRPCMethod.HTTPCommand httpCommand,
                            String resourcePath,
                            Method method) {
            enum HTTPCommand {GET, POST}

        HTTPCommand getHTTPCommand() {
                return httpCommand;
            }
        }

    List<RESTfulRPCMethod> getGETCommands() {

        return getPublicMethods(object.getClass())
                .stream()
                .filter(element -> !Modifier.isStatic(element.getModifiers())) //Convention for all exposed methods
                .filter(element -> !(element.getReturnType().equals(void.class)) &&
                        element.getParameterCount() == 0) // Convention for GET method
                .map(element ->
                        new RESTfulRPCMethod(
                                RESTfulRPCMethod.HTTPCommand.GET,
                                generateURI(element),
                                element))
                .toList();
    }


    List<RESTfulRPCMethod> getPOSTCommands() {

        return getPublicMethods(object.getClass())
                .stream()
                .filter(element -> !Modifier.isStatic(element.getModifiers())) //Convention for all exposed methods
                .filter(element -> (element.getReturnType().equals(void.class) ||
                        element.getParameterCount() > 0)) // Convention for POST method
                .map(element ->
                        new RESTfulRPCMethod(
                                RESTfulRPCMethod.HTTPCommand.POST,
                                generateURI(element),
                                element))
                .toList();
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

    public static RESTfulRPCConvention createRPCConvention(Object object)
    {
        return new RESTfulRPCConvention(object);
    }
}
