package io.jexxa.jexxatest.architecture;


import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static io.jexxa.jexxatest.architecture.PackageName.AGGREGATE;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.BUSINESS_EXCEPTION;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_EVENT;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_WORKFLOW;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVEN_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVING_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.INFRASTRUCTURE;
import static io.jexxa.jexxatest.architecture.PackageName.VALUE_OBJECT;

/**
 * These tests validate the access direction af an onion architecture which is as follows:
 *
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
 */
public class OnionArchitecture extends ArchitectureRule {

    @SuppressWarnings("unused")
    public OnionArchitecture(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }

    protected OnionArchitecture(Class<?> project, ImportOption importOption)
    {
        super(project, importOption);
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

    protected void validatePackageStructure() {
        // Arrange -

        // Act
        var rule = classes().should()
                .resideInAnyPackage(
                        APPLICATIONSERVICE,
                        DOMAIN_WORKFLOW,
                        DOMAIN_SERVICE,
                        AGGREGATE,
                        BUSINESS_EXCEPTION,
                        DOMAIN_EVENT,
                        VALUE_OBJECT,
                        INFRASTRUCTURE)
                .orShould().haveFullyQualifiedName(project().getName());

        //Assert
        rule.check(importedClasses());
    }

    protected void validateApplicationServiceDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(APPLICATIONSERVICE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("An ApplicationService must not depend on other ApplicationServices or the infrastructure");

        //Assert
        invalidAccess.check(importedClasses());
    }


    protected void validateDomainProcessServiceDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DOMAIN_WORKFLOW)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("A DomainProcessService must not depend on an ApplicationServices or the infrastructure");


        //Assert
        invalidAccess.check(importedClasses());
    }

    protected void validateAggregateDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(AGGREGATE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE,
                        DOMAIN_SERVICE,
                        DOMAIN_WORKFLOW,
                        INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("An Aggregate must not depend on any Service or the infrastructure");

        //Assert
        invalidAccess.check(importedClasses());
    }

    protected void validateValueObjectDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(VALUE_OBJECT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE,
                        DOMAIN_SERVICE,
                        DOMAIN_WORKFLOW,
                        INFRASTRUCTURE,
                        AGGREGATE,
                        DOMAIN_EVENT,
                        BUSINESS_EXCEPTION)
                .allowEmptyShould(true)
                .because("A ValueObject must not depend on any other classes of the application except of ValueObjects");


        //Assert
        invalidAccess.check(importedClasses());
    }

    protected void validateDomainEventDependencies() {
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
                        DOMAIN_WORKFLOW,
                        INFRASTRUCTURE)
                .allowEmptyShould(true)
                .because("A DomainEvent must not depend on any other classes of the application except of ValueObjects");

        //Assert
        invalidAccess.check(importedClasses());
    }

    protected void  validateDrivingAdapterDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DRIVING_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage(DRIVEN_ADAPTER)
                .allowEmptyShould(true);

        //Assert
        invalidAccess.check(importedClasses());
    }

    protected void validateDrivenAdapterDependencies() {
        // Arrange -

        // Act
        var invalidAccess = noClasses()
                .that().resideInAPackage(DRIVEN_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage(DRIVING_ADAPTER,
                        APPLICATIONSERVICE,
                        DOMAIN_WORKFLOW
                        );

        //Assert
        invalidAccess.check(importedClasses());
    }

}