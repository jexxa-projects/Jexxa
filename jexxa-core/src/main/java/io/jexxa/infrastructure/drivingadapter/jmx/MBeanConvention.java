package io.jexxa.infrastructure.drivingadapter.jmx;


import static io.jexxa.utils.json.JSONManager.getJSONConverter;
import static java.util.stream.Collectors.toList;
import static javax.management.MBeanOperationInfo.UNKNOWN;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.google.gson.JsonObject;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.JexxaLogger;

public class MBeanConvention implements DynamicMBean
{
    public static final String JEXXA_CONTEXT_NAME = "io.jexxa.context.name";

    private final Object object;
    private final String contextName;

    MBeanConvention(Object object, Properties properties)
    {
        this.object = Objects.requireNonNull(object);
        contextName = Objects.requireNonNull(properties).getProperty(JEXXA_CONTEXT_NAME, "UnknownContext");
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
    @SuppressWarnings({"java:S112", "java:S2139"})
    public Object invoke(String actionName, Object[] params, String[] signature)
    {
        try
        {
            var method = getMethod(actionName).
                orElseThrow(UnsupportedOperationException::new);

            Object[] parameter = deserializeObjects(method.getParameterTypes(), params);
            Object result = IDrivingAdapter
                    .acquireLock()
                    .invoke(method, object, parameter);

            return serializeComplexReturnValue(result);
        }
        catch (Exception e)
        {
            String errorMessage;
            if ( e.getCause() != null)
            {
                errorMessage = e.getCause().getMessage();
            } else {
                errorMessage = e.getMessage();
            }

            JexxaLogger.getLogger(JMXAdapter.class).error("{} occurred when processing method `{}`", e.getClass().getSimpleName(), actionName);
            JexxaLogger.getLogger(JMXAdapter.class).error("Exception message: {}", errorMessage);
            throw new IllegalArgumentException(e);
        }
    }

    private Object[] deserializeObjects(Class<?>[] parameterTypes, Object[] parameters)
    {
        if (parameterTypes == null || parameterTypes.length == 0)
        {
            return new Object[0];
        }

        if (parameters.length != parameterTypes.length)
        {
            throw new IllegalArgumentException("Invalid number of parameter");
        }

        var result = new Object[parameters.length];

        for (var i = 0; i < parameters.length; ++i)
        {
            result[i] = getJSONConverter().fromJson((String) parameters[i], parameterTypes[i]);
        }

        return result;
    }


    public MBeanInfo getMBeanInfo()
    {
        return new MBeanInfo(
                object.getClass().getSimpleName(),
                contextName,
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
        }
        catch (MalformedObjectNameException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    private MBeanOperationInfo[] getMBeanOperation()
    {
        var methodList = Arrays.stream(object.getClass().getMethods())
                .filter( method -> !Modifier.isStatic(method.getModifiers())) //Filter all static methods
                .collect(toList());

        //Get methods only from concrete type => Exclude all methods from Object
        methodList.removeAll(Arrays.asList(Object.class.getMethods()));

        return methodList.
                stream()
                .map(method -> new MBeanOperationInfo(
                        method.getName(),
                        method.getName(),
                        getMBeanParameterInfo(method),
                        method.getReturnType().getName(),
                        UNKNOWN,
                        null))
                .toArray(MBeanOperationInfo[]::new);
    }

    protected String getDomainPath()
    {
        // Build domainPath for jmx as follows for better grouping (e.g., in JConsole)
        // The grouping is:  <ContextName> -> <First Annotation of object (if available) > -> <simple name of object>
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

    protected String toJsonTemplate(Class<?> clazz)
    {
        if ( clazz.isPrimitive() || clazz.isAssignableFrom(String.class))
        {
            return "<"+clazz.getSimpleName()+">";
        }

        return complexTypeToJsonTemplate(clazz);
    }

    private String complexTypeToJsonTemplate(Class<?> clazz)
    {
        var jsonObject = new JsonObject();

        if ( clazz.isPrimitive() || clazz.isAssignableFrom(String.class))
        {
            jsonObject.addProperty(clazz.getSimpleName(), "<"+clazz.getSimpleName()+">");
            return jsonObject.toString();
        }

        Arrays.stream(filterFieldsForJson(clazz)).
                forEach( field -> toJsonTemplate(field,jsonObject));

        return jsonObject.toString();
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


    private Object serializeComplexReturnValue(Object object)
    {
        if ( object == null ||
             object.getClass().isPrimitive()
        )
        {
            return object;
        }

        return getJSONConverter().toJson(object);
    }

    private Field[] filterFieldsForJson(Class<?> clazz)
    {
        // Get fields we have to process => Ignore synthetic fields because they are generated by compiler
        //                               => Ignore static fields which could case infinite loop if a class provide static elements of itself
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(declaredField -> !declaredField.isSynthetic())
                .filter(declaredField -> !Modifier.isStatic(declaredField.getModifiers()))
                .toArray(Field[]::new);
    }

    private void toJsonTemplate(Field field, JsonObject parent )
    {
        //Terminate recursion in case we find a java base type (currently defined as type of java.lang
        if ( (field.getType().isPrimitive() || field.getType().isAssignableFrom(String.class) )
                && !Modifier.isStatic(field.getModifiers()))
        {
            parent.addProperty(field.getName(), "<"+field.getType().getSimpleName()+">");
            return;
        }


        if (filterFieldsForJson(field.getType()).length > 0 )
        {
            var child = new JsonObject();
            parent.add(field.getName(), child);
            Arrays.stream(filterFieldsForJson(field.getType())).
                    forEach(attribute -> toJsonTemplate(attribute, child));
        }

    }


    private Optional<Method> getMethod(String name)
    {
        return Arrays.stream(object.getClass().getMethods())
                .filter( method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getName().equals(name))
                .findFirst();
    }

    private Optional<Annotation> getFirstAnnotation()
    {
        return Arrays.stream(object.getClass().getDeclaredAnnotations())
                .findFirst();
    }

}
