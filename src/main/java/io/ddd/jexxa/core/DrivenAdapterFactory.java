package io.ddd.jexxa.core;


import java.util.Properties;

import org.apache.commons.lang.Validate;

/*
 * Rules for creating a driving adapter:
 * 1. Public Default constructor available
 * 2. Public constructor with Properties as attribute
 * 3. Public static method with return type if the requested interface
 * 4. Public static method with return type if the requested interface and Properties as argument
 */
public class DrivenAdapterFactory
{
    public <T> T createDrivenAdapter(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        return interfaceType.cast(ClassFactory.createByConstructor(implementation));
    }

    public <T> T createDrivenAdapter(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        return interfaceType.cast(ClassFactory.createByConstructor(implementation, properties));
    }

    private <T> Class<?> getImplementationOf(Class<T> interfaceType) {
        var dependencyScanner = new DependencyScanner();
        var results = dependencyScanner.getClassesImplementing(interfaceType);
        Validate.notNull(results);
        Validate.notEmpty(results, "No implementation of " + interfaceType.getName() + " available");
        Validate.isTrue( results.size() == 1, "Multiple implementation of " + interfaceType.getName() + " available");

        return results.get(0);
    }


}
