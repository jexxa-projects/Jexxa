package io.jexxa.core.factory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.Validate;

final class DependencyScanner
{
    private final List<String> whiteListPackages = new ArrayList<>();
    private ScanResult scanResult;

    DependencyScanner whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        scanResult = null; //Reset scan result so that it is recreated with new white listed packages
        return this;
    }

    DependencyScanner whiteListPackages(List<String> packageList)
    {
        whiteListPackages.addAll(packageList);
        scanResult = null; //Reset scan result so that it is recreated with new white listed packages
        return this;
    }


    List<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> annotation)
    {
        validateRetentionRuntime(annotation);

        return getScanResult()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }


    List<Class<?>> getClassesImplementing(final Class<?> interfaceType)
    {
        Validate.notNull(interfaceType);
        return getScanResult()
                    .getClassesImplementing(interfaceType.getName())
                    .loadClasses();
    }
    


    private void validateRetentionRuntime(final Class<? extends Annotation> annotation) {
        Validate.notNull(annotation.getAnnotation(Retention.class), "Annotation must be declared with '@Retention(RUNTIME)'" );
        Validate.isTrue(annotation.getAnnotation(Retention.class).value().equals(RetentionPolicy.RUNTIME), "Annotation must be declared with '@Retention(RUNTIME)");
    }

    private ScanResult getScanResult()
    {
        if ( scanResult == null )
        {
            if (whiteListPackages.isEmpty())
            {
                scanResult = new ClassGraph()
                        .enableAnnotationInfo()
                        .enableClassInfo()
                        .scan();
            }
            else
            {
                scanResult = new ClassGraph()
                        .enableAnnotationInfo()
                        .enableClassInfo()
                        .acceptPackages(whiteListPackages.toArray(new String[0]))
                        .scan();

            }
        }

        return scanResult;
    }

}
