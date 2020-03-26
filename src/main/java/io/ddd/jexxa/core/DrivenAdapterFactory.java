package io.ddd.jexxa.core;


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
    private ClassFactory classFactory = new ClassFactory(null);

    public <T> T createDrivenAdapter(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        return interfaceType.cast(classFactory.createByConstructor(implementation));
    }

    private <T> Class<?> getImplementationOf(Class<T> interfaceType) {
        var dependencyScanner = new DependencyScanner();
        var results = dependencyScanner.getClassesImplementing(interfaceType);
        Validate.notNull(results);
        Validate.isTrue(results.size() == 1, "Multiple implementation of " + interfaceType.getName() + " available");

        return results.get(0);
    }


}
