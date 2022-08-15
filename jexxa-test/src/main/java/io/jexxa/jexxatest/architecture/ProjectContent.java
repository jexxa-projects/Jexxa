package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

@SuppressWarnings("unused")
public abstract class ProjectContent
{
    private final Class<?> project;
    private JavaClasses importedClasses;

    protected ProjectContent(Class<?> project, ImportOption importOption)
    {
        this.project = project;
        importedClasses = new ClassFileImporter()
                .withImportOption(importOption)
                .importPackages(
                        project.getPackageName()+ ".domain..",
                        project.getPackageName()+ ".domainservice..",
                        project.getPackageName()+ ".applicationservice..",
                        project.getPackageName()+ ".infrastructure.." );
    }

    public ProjectContent ignoreClass(Class<?> clazz)
    {
        importedClasses = importedClasses.that(isNot(clazz));
        return this;
    }

    public abstract void validate();

    protected JavaClasses importedClasses()
    {
        return importedClasses;
    }

    protected Class<?> project()
    {
        return project;
    }
    private static DescribedPredicate<JavaClass> isNot(Class<?>clazz) {
        return new DescribedPredicate<>("Ignore class " + clazz.getSimpleName()) {
            @Override
            public boolean apply(JavaClass input) {
                return !input.isEquivalentTo(clazz);
            }
        };
    }

}
