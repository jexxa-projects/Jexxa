package io.jexxa.core.factory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class DependencyScanner
{
    private final List<String> acceptedPackages = new ArrayList<>();
    private ScanResult scanResult;

    DependencyScanner acceptPackage(String packageName)
    {
        acceptedPackages.add(packageName);
        scanResult = null; //Reset scan result so that it is recreated with new accepted packages
        return this;
    }

    List<String> getAcceptPackages()
    {
        return acceptedPackages;
    }

    List<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> annotation)
    {
        validateRetentionRuntime(annotation);

        return load(
                getScanResult().getClassesWithAnnotation(annotation.getName()),
                scanResult
        );
    }

    List<Class<?>> getClassesImplementing(final Class<?> interfaceType)
    {
        Objects.requireNonNull(interfaceType);

        return load(
                getScanResult().getAllClassesImplementing(interfaceType.getName()),
                scanResult
        );
    }

    private void validateRetentionRuntime(final Class<? extends Annotation> annotation) {
        Objects.requireNonNull(annotation.getAnnotation(Retention.class), "Annotation must be declared with '@Retention(RUNTIME)'" );
        if (!annotation.getAnnotation(Retention.class).value().equals(RetentionPolicy.RUNTIME))
        {
            throw new IllegalArgumentException("Annotation must be declared with '@Retention(RUNTIME)");
        }
    }

    private ScanResult getScanResult()
    {
        if ( scanResult == null )
        {
            if (acceptedPackages.isEmpty())
            {
                scanResult = new ClassGraph()
                        .enableAnnotationInfo()
                        .enableClasspath()
                        .enableClassInfo()
                        .scan();
            }
            else
            {
                scanResult = new ClassGraph()
                        .enableAnnotationInfo()
                        .enableClassInfo()
                        .enableClasspath()
                        .acceptPackages(acceptedPackages.toArray(new String[0]))
                        .scan();

            }
        }
        return scanResult;
    }

    private static List<Class<?>> load(ClassInfoList list, ScanResult scanResult) {
        java.net.URL[] urls = scanResult.getClasspathURLs().toArray(new java.net.URL[0]);
        ClassLoader parent = Thread.currentThread().getContextClassLoader();

        try (java.net.URLClassLoader loader = new java.net.URLClassLoader(urls, parent)) {
            return list.stream()
                    .<Class<?>>map(info -> {
                        try {
                            return Class.forName(info.getName(), false, loader);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Klasse fehlt: " + info.getName(), e);
                        }
                    })
                    .toList();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Fehler beim Schließen des ClassLoaders", e);
        }
    }
}
