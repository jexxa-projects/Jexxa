package io.jexxa.jexxatest.architecture;


import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static io.jexxa.jexxatest.architecture.PackageName.AGGREGATE;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.BUSINESS_EXCEPTION;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_EVENT;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_PROCESS_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVEN_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVING_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.INFRASTRUCTURE;
import static io.jexxa.jexxatest.architecture.PackageName.VALUE_OBJECT;

/**
 * These tests validate the access direction af an onion architecture which is as follows:
 *
 * { @code
 * @startuml
 *
 * package ApplicationCore  #DDDDDD {
 *   [ApplicationService]
 *   [DomainWorkflow] <<Optional>>
 *   [DomainService]
 *   [Domain]
 * }
 *
 * [ApplicationService] -down-> [DomainWorkflow]
 * [ApplicationService] -down-> [DomainService]
 * [ApplicationService] -down-> [Domain]
 * [DomainWorkflow] -down-> [DomainService]
 * [DomainWorkflow] -down-> [Domain]
 * [DomainService] -r-> [Domain]
 *
 * @enduml
 * ....
 * }
 */
public class OnionArchitecture {
    private final Class<?> project;
    private final JavaClasses importedClasses;


    public OnionArchitecture(Class<?> project)
    {
        this.project = project;
        importedClasses = new ClassFileImporter()
                .importPackages(project.getPackage().getName());
    }

    public void validate()
    {
        validatePackageStructure();

        validateApplicationServiceDependencies();
        validateDomainProcessServiceDependencies();
        validateAggregateDependencies();
        validateValueObjectDependencies();
        validateDomainEventDependencies();

        validateDrivingAdapterDependencies();
        validateDrivenAdapterDependencies();
    }

    void validatePackageStructure() {
        // Arrange -

        // Act
        var rule = classes().should()
                .resideInAnyPackage(
                        APPLICATIONSERVICE,
                        DOMAIN_PROCESS_SERVICE,
                        DOMAIN_SERVICE,
                        AGGREGATE,
                        BUSINESS_EXCEPTION,
                        DOMAIN_EVENT,
                        VALUE_OBJECT,
                        INFRASTRUCTURE)
                .orShould().haveFullyQualifiedName(project.getName());

        //Assert
        rule.check(importedClasses);
    }

    void validateApplicationServiceDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(APPLICATIONSERVICE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("An ApplicationService must not depend on other ApplicationServices or the infrastructure");

        //Assert
        invalidAccess.check(importedClasses);
    }


    void validateDomainProcessServiceDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DOMAIN_PROCESS_SERVICE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("A DomainProcessService must not depend on an ApplicationServices or the infrastructure");


        //Assert
        invalidAccess.check(importedClasses);
    }

    void validateAggregateDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(AGGREGATE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE,
                        DOMAIN_SERVICE,
                        DOMAIN_PROCESS_SERVICE,
                        INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("An Aggregate must not depend on any Service or the infrastructure");

        //Assert
        invalidAccess.check(importedClasses);
    }

    void validateValueObjectDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(VALUE_OBJECT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE,
                        DOMAIN_SERVICE,
                        DOMAIN_PROCESS_SERVICE,
                        INFRASTRUCTURE,
                        AGGREGATE,
                        DOMAIN_EVENT,
                        BUSINESS_EXCEPTION)
                .allowEmptyShould(true)
                .because("A ValueObject must not depend on any other classes of the application except of ValueObjects");


        //Assert
        invalidAccess.check(importedClasses);
    }

    void validateDomainEventDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DOMAIN_EVENT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        APPLICATIONSERVICE,
                        BUSINESS_EXCEPTION,
                        AGGREGATE,
                        DOMAIN_SERVICE,
                        DOMAIN_PROCESS_SERVICE,
                        INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("A DomainEvent must not depend on any other classes of the application except of ValueObjects");

        //Assert
        invalidAccess.check(importedClasses);
    }

    void  validateDrivingAdapterDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DRIVING_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage(DRIVEN_ADAPTER)
                .allowEmptyShould(true);

        //Assert
        invalidAccess.check(importedClasses);
    }

    void validateDrivenAdapterDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DRIVEN_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage(DRIVING_ADAPTER,
                        APPLICATIONSERVICE,
                        DOMAIN_PROCESS_SERVICE
                        );

        //Assert
        invalidAccess.check(importedClasses);
    }

}