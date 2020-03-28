package io.ddd.jexxa.core.factory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.github.classgraph.ClassGraph;
import org.apache.commons.lang.Validate;

//TODO: Double check if it is a good practice to cache scan results
class DependencyScanner
{
    private List<String> whiteListPackages = new ArrayList<>();

    DependencyScanner whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    DependencyScanner whiteListPackages(List<String> packageList)
    {
        whiteListPackages.addAll(packageList);
        return this;
    }


    List<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> annotation)
    {
        validateRetentionRuntime(annotation);

        if (whiteListPackages.isEmpty())
        {
            return new ClassGraph()
                    //.verbose()
                    .enableAllInfo()
                    .scan()
                    .getClassesWithAnnotation(annotation.getName())
                    .loadClasses();
        }

        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .whitelistPackages( whiteListPackages.toArray(new String[0]))
                .scan()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }


    List<Class<?>> getClassesImplementing(final Class<?> interfaceType)
    {
        Validate.notNull(interfaceType);

        if (whiteListPackages.isEmpty())
        {
            return new ClassGraph()
                    //.verbose()
                    .enableAllInfo()
                    .scan()
                    .getClassesImplementing(interfaceType.getName())
                    .loadClasses();
        }

        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .whitelistPackages( whiteListPackages.toArray(new String[0]))
                .scan()
                .getClassesImplementing(interfaceType.getName())
                .loadClasses();
    }
    


    private void validateRetentionRuntime(final Class<? extends Annotation> annotation) {
        Validate.notNull(annotation.getAnnotation(Retention.class), "Annotation must be declared with '@Retention(RUNTIME)'" );
        Validate.isTrue(annotation.getAnnotation(Retention.class).value().equals(RetentionPolicy.RUNTIME), "Annotation must be declared with '@Retention(RUNTIME)");
    }

}
