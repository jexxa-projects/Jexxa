package io.ddd.jexxa.infrastructure;

import java.lang.annotation.Annotation;
import java.util.List;

import io.github.classgraph.ClassGraph;

public class AnnotationScanner
{

    public List<Class<?>> findClassAnnotation(final Class<? extends Annotation> annotation)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .scan()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }

    public List<Class<?>> findClassAnnotation(final Class<? extends Annotation> annotation, String packageName)
    {
        return new ClassGraph()
                //.verbose()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .getClassesWithAnnotation(annotation.getName())
                .loadClasses();
    }

}
