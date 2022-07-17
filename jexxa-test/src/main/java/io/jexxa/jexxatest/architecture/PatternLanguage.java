package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.addend.applicationcore.BusinessException;
import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.addend.applicationcore.DomainProcessStep;
import io.jexxa.addend.applicationcore.DomainService;
import io.jexxa.addend.applicationcore.DomainWorkflow;
import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.addend.applicationcore.ValueObject;
import io.jexxa.addend.applicationcore.ValueObjectFactory;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jexxatest.architecture.PackageName.AGGREGATE;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.BUSINESS_EXCEPTION;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_EVENT;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_PROCESS_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.VALUE_OBJECT;

public class PatternLanguage {

    private final JavaClasses importedClasses;


    public PatternLanguage(Class<?> project)
    {
        importedClasses = new ClassFileImporter()
                .importPackages(project.getPackage().getName());
    }

    public void validate()
    {
        validateAnnotationApplicationService();
        validateAnnotationDomainService();
        validateAnnotationDomainProcessService();
        validateAnnotationDomainEvent();
        validateAnnotationValueObject();
        validateAnnotationBusinessException();
        validateAnnotationAggregate();

        validateRepositoryMustBeInterfaces();
        validateInfrastructureServiceMustBeInterfaces();
        validateValueObjectMustBeRecords();
        validateDomainEventMustBeRecord();
    }

    void validateAnnotationApplicationService()
    {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(APPLICATIONSERVICE)
                .should().beAnnotatedWith(ApplicationService.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationDomainService() {
        // Arrange

        //Act
        var annotationRule = classes().that().resideInAnyPackage(DOMAIN_SERVICE)
                .should().beAnnotatedWith(Repository.class)
                .orShould().beAnnotatedWith(InfrastructureService.class)
                .orShould().beAnnotatedWith(DomainService.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationDomainProcessService() {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_PROCESS_SERVICE)
                .should().beAnnotatedWith(DomainProcessStep.class)
                .orShould().beAnnotatedWith(DomainWorkflow.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationDomainEvent() {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .should().beAnnotatedWith(DomainEvent.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationValueObject() {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(VALUE_OBJECT)
                .should().beAnnotatedWith(ValueObject.class)
                .orShould().beAnnotatedWith(ValueObjectFactory.class)
                .orShould().beEnums()
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationBusinessException() {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(BUSINESS_EXCEPTION)
                .should().beAnnotatedWith(BusinessException.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateAnnotationAggregate() {
        // Arrange

        //Act
        var annotationRule = classes()
                .that().resideInAnyPackage(AGGREGATE)
                .and().areNotAnonymousClasses()
                .and().areNotInnerClasses()
                .should().beAnnotatedWith(Aggregate.class)
                .orShould().beAnnotatedWith(FunctionalInterface.class)
                .allowEmptyShould(true);

        //Assert
        annotationRule.check(importedClasses);
    }

    void validateRepositoryMustBeInterfaces() {
        // Arrange

        //Act
        var interfaceRule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        //Assert
        interfaceRule.check(importedClasses);
    }

    void validateInfrastructureServiceMustBeInterfaces() {
        // Arrange

        //Act
        var interfaceRule = classes()
                .that().areAnnotatedWith(InfrastructureService.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        //Assert
        interfaceRule.check(importedClasses);
    }

    void validateValueObjectMustBeRecords() {
        // Arrange

        //Act
        var recordRule = classes()
                .that().resideInAnyPackage(VALUE_OBJECT)
                .and().areNotAnnotatedWith(ValueObjectFactory.class)
                .should().beRecords()
                .orShould().beEnums()
                .allowEmptyShould(true);

        //Assert
        recordRule.check(importedClasses);
    }

    void validateDomainEventMustBeRecord() {
        // Arrange

        //Act
        var recordRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .should().beRecords()
                .allowEmptyShould(true);

        //Assert
        recordRule.check(importedClasses);
    }

}

