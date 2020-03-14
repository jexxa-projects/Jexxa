package io.ddd.jexxa.infrastructure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class AnnotationScanner
{
    private String packageName;



    public List<Class<?>> findAnnotation(final Class<? extends Annotation> annotation)
    {
        ScanResult scanResult;

        if (packageName != null) {
            scanResult = new ClassGraph()
                    //.verbose()
                    .enableAllInfo()
                    .whitelistPackages(packageName)
                    .scan();
        } else {
            scanResult = new ClassGraph()
                    //.verbose()
                    .enableAllInfo()
                    .scan();
        }

        return scanResult.getClassesWithAnnotation(annotation.getName()).loadClasses();
    }


    public AnnotationScanner(String packageName)
    {
        this.packageName = packageName;
    }

   /* public Object createCorrespondingObject(Method method)
    {
        Class<?> neededClass = method.getDeclaringClass();
        for (Class<?> clazz : factories)
        {
            for (Method erzeuge : clazz.getMethods())
            {
                if (erzeuge.getReturnType() == neededClass)
                {
                    try
                    {
                        Object factoryObject = clazz.getConstructors()[0].newInstance(PropertiesLoader.getInstance().getProperties());
                        return erzeuge.invoke(factoryObject);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new Exception("Objekt konnte nicht erzeugt werden");
    }*/

    public AnnotationScanner()
    {
    }

}
