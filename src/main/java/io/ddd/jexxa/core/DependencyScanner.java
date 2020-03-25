package io.ddd.jexxa.core;

import java.lang.annotation.Annotation;
import java.util.List;

import io.github.classgraph.ClassGraph;

public class DependencyScanner
{

    public List<Class<?>> getClassAnnotation(final Class<? extends Annotation> annotation)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .scan()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }

    public List<Class<?>> getClassAnnotation(final Class<? extends Annotation> annotation, String packageName)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }

    public List<Class<?>> getClassesImplementing(final Class<?> interfaceType)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .scan()
                .getClassesImplementing(interfaceType.getName())
                .loadClasses();
        
    }

    public List<Class<?>> getClassesImplementing(final Class<?> interfaceType, String packageName)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .getClassesImplementing(interfaceType.getName())
                .loadClasses();

    }

}
