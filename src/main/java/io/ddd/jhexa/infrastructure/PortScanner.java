package io.ddd.jhexa.infrastructure;

import java.lang.annotation.Annotation;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class PortScanner
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


    public PortScanner(String packageName)
    {
        this.packageName = packageName;
    }

    public PortScanner()
    {
    }

}
