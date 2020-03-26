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
    public <T> T create(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        //Apply 1. convention and try to use default constructor
        var instance = interfaceType.cast(ClassFactory.createByConstructor(implementation));


        //Apply 2. convention and try to use a factory method 
        if (instance == null) {
            instance = interfaceType.cast(ClassFactory.createByFactoryMethod(implementation, interfaceType));
        }
        
        Validate.notNull(instance, "No suitable constructor found to create " + interfaceType.getName());
        
        return instance;
    }

    public <T> T create(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        //Apply 1. convention and try to use a constructor accepting properties 
        T instance = interfaceType.cast(ClassFactory.createByConstructor(implementation, properties));

        //Apply 2. convention and try to use a factory method accepting properties
        if (instance == null) {
            instance = interfaceType.cast(ClassFactory.createByFactoryMethod(implementation, interfaceType, properties));
        }

        Validate.notNull(instance, "No suitable constructor found to create " + interfaceType.getName());

        return instance;
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
