package io.ddd.jexxa.infrastructure.drivingadapter.jmx;


import static javax.management.MBeanOperationInfo.UNKNOWN;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.ddd.jexxa.utils.JexxaLogger;

public class MBeanModel implements DynamicMBean
{
    public static final String CONTEXT_NAME = "io.ddd.jexxa.context.name";
    private Gson gson = new Gson();

    private final Object object;
    String contextName;
    
    MBeanModel(Object object, Properties properties)
    {
        this.object = object;

        if ( properties != null)
        {
            contextName = properties.getProperty(CONTEXT_NAME);
        }

        if ( contextName == null ) {
            contextName = "UnknownContext";
        }
    }


    @Override
    public Object getAttribute(String attribute)
    {
        return null;  // We don't offer access to attributes
    }

    @Override
    public void setAttribute(Attribute attribute)
    {
        throw new UnsupportedOperationException();    //We don't offer access to attributes
    }

    @Override
    public AttributeList getAttributes(String[] attributes)
    {
        return new AttributeList(); //We don't offer access to attributes
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes)
    {
        throw new UnsupportedOperationException();    //We don't offer access to attributes
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
    {
        var method = getMethod(actionName);

        if ( method.isPresent() &&  method.get().getParameterCount() == 0) {
            try
            {
                return method.get().invoke(object);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                JexxaLogger.getLogger(getClass()).error(e.getMessage());
                return null;
            }
        }


        if ( method.isPresent() &&  method.get().getParameterCount() > 0) {
            try
            {
                Object [] parameter = deserializeObjects(method.get().getParameterTypes(), params);
                return method.get().invoke(object, parameter);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                JexxaLogger.getLogger(getClass()).error(e.getMessage());
                return null;
            }
        }

        return null;
    }

    Object[] deserializeObjects(Class<?>[] parameterTypes, Object[] parameters)
    {
        if ( parameters.length != parameterTypes.length) {
            throw new IllegalArgumentException("Invalid number of parameter");
        }

        Object[] result = new Object[parameters.length];

        for (int i = 0; i < parameters.length; ++i) {
            result[i] = gson.fromJson((String) parameters[i], parameterTypes[i]);
        }

        return result;
    }



    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo(
                object.getClass().getSimpleName(),
                "Hello Jexxa",
                null,                            
                null,
                getMBeanOperation(),
                null
        );

    }

    public ObjectName getObjectName()
    {
        try
        {
           return new ObjectName(getDomainPath());
        } catch (Exception e)
        {
           throw new IllegalArgumentException(e.getMessage());
        }
    }

    MBeanOperationInfo[] getMBeanOperation()
    {
        var methodList = Arrays.stream(object.getClass().getMethods()).
                collect(Collectors.toList());

        //Get methods only from concrete type => Exclude all methods from Object
        methodList.removeAll(Arrays.asList(Object.class.getMethods()));

        return methodList.
                stream().
                map(method -> new MBeanOperationInfo(
                        method.getName(),
                        method.getName(),
                        getMBeanParameterInfo(method),
                        method.getReturnType().getName(),
                        UNKNOWN,
                        null)).
                toArray(MBeanOperationInfo[]::new);
    }

    private MBeanParameterInfo[] getMBeanParameterInfo(Method method)
    {
        return Arrays.
                stream(method.getParameterTypes()).
                map(this::getMBeanParameter).
                toArray(MBeanParameterInfo[]::new);
    }

    private MBeanParameterInfo getMBeanParameter(Class<?> parameter)
    {
        return new MBeanParameterInfo(
                parameter.getSimpleName(),
                String.class.getName(),
                toJsonTemplate(parameter)
        );
    }

    String getDomainPath()
    {
        //Build domainPath for jmx as follows for better grouping (e.g., in JConsole)
        //   <ContextName> -> <First Annotation of object (if available) > -> <simple name of object>
        var stringBuilder = new StringBuilder();

        stringBuilder.
                append(contextName).
                append(":");

        getFirstAnnotation().ifPresent(
                annotation -> stringBuilder.
                        append("type=").
                        append(annotation.annotationType().getSimpleName()).
                        append(",")
        );

        stringBuilder.
                append("name=").
                append(object.getClass().getSimpleName());

        return stringBuilder.toString();
    }


    String toJsonTemplate(Class<?> clazz )
    {
        JsonObject jsonObject = new JsonObject();

        if ( clazz.getPackageName().startsWith("java.lang"))
        {
            jsonObject.addProperty(clazz.getSimpleName(), "value");
            return jsonObject.toString();
        }

        Arrays.stream(clazz.getDeclaredFields()).
                forEach( field -> toJsonTemplate(field,jsonObject));

        return jsonObject.toString();
    }

    private void toJsonTemplate(Field field, JsonObject parent )
    {
        //Terminate recursion in case we find a java base type (currently defined as type of java.lang
        if ( field.getType().getPackageName().startsWith("java.lang"))
        {
            parent.addProperty(field.getName(), field.getType().getSimpleName());
            return;
        }

        // Terminate recursion if we fields with size 0 which is typically from nested Classes
        if ( field.getType().getDeclaredFields().length == 0) {
            return;
        }

        JsonObject child = new JsonObject();
        parent.add(field.getName(), child);
        Arrays.stream(field.getType().getDeclaredFields()).
                forEach(attribute -> toJsonTemplate(attribute, child));

    }
    

    private Optional<Method> getMethod(String name)
    {
        return Arrays.
                stream(object.getClass().getMethods()).
                filter(method -> method.getName().equals(name)).
                findFirst();
    }

    private Optional<Annotation> getFirstAnnotation()
    {
        return Arrays.
                stream(object.getClass().getDeclaredAnnotations()).
                findFirst();
    }

}
