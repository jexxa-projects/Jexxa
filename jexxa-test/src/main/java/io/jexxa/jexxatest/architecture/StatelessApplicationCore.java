package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.Repository;

import java.util.List;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static io.jexxa.jexxatest.architecture.PackageName.AGGREGATE;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.BUSINESS_EXCEPTION;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_WORKFLOW;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.VALUE_OBJECT;

public class StatelessApplicationCore {

    private final JavaClasses importedClasses;


    public StatelessApplicationCore(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }

    StatelessApplicationCore(Class<?> project, ImportOption importOption)
    {
        importedClasses = new ClassFileImporter()
                .withImportOption(importOption)
                .importPackages(project.getPackage().getName());
    }


    public void validate()
    {
        validateApplicationCoreDoesNotHaveStatefulFields();
        validateFinalFields();
        validateOnlyRepositoriesAcceptAggregates();
        validateOnlyRepositoriesReturnAggregates();
    }
    
    void validateOnlyRepositoriesReturnAggregates() {
        // Arrange -

        // Act
        var invalidReturnType = noMethods().that()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, DOMAIN_SERVICE))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawReturnType(resideInAnyPackage(AGGREGATE))
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be returned by a Repository!");


        //Assert
        invalidReturnType.check(importedClasses);
    }

    
    void validateOnlyRepositoriesAcceptAggregates() {
        // Arrange -

        // Act
        var invalidReturnType = noMethods().that()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, DOMAIN_SERVICE))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawParameterTypes(thatAreAggregates())
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be returned by a Repository!");


        //Assert
        invalidReturnType.check(importedClasses);
    }


    
    void validateFinalFields() {
        // Arrange -

        // Act
        var finalFields = fields().that().areDeclaredInClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, BUSINESS_EXCEPTION, DOMAIN_SERVICE, VALUE_OBJECT)
                .and().areDeclaredInClassesThat().areNotNestedClasses()
                .should().beFinal()
                .allowEmptyShould(true)
                .because("The application core must be stateless except of Aggregates!");


        //Assert
        finalFields.check(importedClasses);
    }

    
    void validateApplicationCoreDoesNotHaveStatefulFields() {
        // Arrange -

        // Act
        var invalidReturnType = noFields().that().areDeclaredInClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, BUSINESS_EXCEPTION, DOMAIN_SERVICE, VALUE_OBJECT)
                .should().haveRawType(resideInAPackage(AGGREGATE))
                .allowEmptyShould(true)
                .because("An ApplicationService or DomainProcessService must not keep a reference to an aggregate!");


        //Assert
        invalidReturnType.check(importedClasses);
    }

    private static DescribedPredicate<List<JavaClass>> thatAreAggregates() {
        return new DescribedPredicate<>("one parameter of type is an Aggregate") {
            @Override
            public boolean apply(List<JavaClass> input) {
                return input.stream().anyMatch(element -> element.isAnnotatedWith(Aggregate.class));
            }
        };
    }
}