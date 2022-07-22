package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
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
import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.addend.infrastructure.DrivingAdapter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jexxatest.architecture.PackageName.AGGREGATE;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.BUSINESS_EXCEPTION;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_EVENT;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_WORKFLOW;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVEN_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVING_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.VALUE_OBJECT;

public class PatternLanguage extends ArchitectureRule {

    @SuppressWarnings("unused")
    public PatternLanguage(Class<?> project)
    {
        this(project,ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }
    protected PatternLanguage(Class<?> project, ImportOption importOption)
    {
        super(project,importOption);
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
        validateAnnotationDrivenAdapter();
        validateAnnotationDrivingAdapter();

        validateRepositoryMustBeInterfaces();
        validateInfrastructureServiceMustBeInterfaces();
        validateValueObjectMustBeRecords();
        validateDomainEventMustBeRecord();
    }


    protected void validateAnnotationApplicationService()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(APPLICATIONSERVICE)
                .should().beAnnotatedWith(ApplicationService.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationDomainService() {
        var annotationRule = classes().that().resideInAnyPackage(DOMAIN_SERVICE)
                .should().beAnnotatedWith(Repository.class)
                .orShould().beAnnotatedWith(InfrastructureService.class)
                .orShould().beAnnotatedWith(DomainService.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationDomainProcessService() {
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_WORKFLOW)
                .should().beAnnotatedWith(DomainProcessStep.class)
                .orShould().beAnnotatedWith(DomainWorkflow.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationDomainEvent() {
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .should().beAnnotatedWith(DomainEvent.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationValueObject() {
        var annotationRule = classes()
                .that().resideInAnyPackage(VALUE_OBJECT)
                .should().beAnnotatedWith(ValueObject.class)
                .orShould().beAnnotatedWith(ValueObjectFactory.class)
                .orShould().beEnums()
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationBusinessException() {
        var annotationRule = classes()
                .that().resideInAnyPackage(BUSINESS_EXCEPTION)
                .should().beAnnotatedWith(BusinessException.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationDrivenAdapter() {
        var annotationRule = classes()
                .that().resideInAnyPackage(DRIVEN_ADAPTER)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().areNotEnums()
                .should().beAnnotatedWith(DrivenAdapter.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateAnnotationDrivingAdapter() {
        var annotationRule = classes()
                .that().resideInAnyPackage(DRIVING_ADAPTER)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().areNotEnums()
                .should().beAnnotatedWith(DrivingAdapter.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }


    protected void validateAnnotationAggregate() {
        var annotationRule = classes()
                .that().resideInAnyPackage(AGGREGATE)
                .and().areNotAnonymousClasses()
                .and().areNotInnerClasses()
                .should().beAnnotatedWith(Aggregate.class)
                .orShould().beAnnotatedWith(FunctionalInterface.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateRepositoryMustBeInterfaces() {
        var interfaceRule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        interfaceRule.check(importedClasses());
    }

    protected void validateInfrastructureServiceMustBeInterfaces() {
        var interfaceRule = classes()
                .that().areAnnotatedWith(InfrastructureService.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        interfaceRule.check(importedClasses());
    }

    protected void validateValueObjectMustBeRecords() {
        var recordRule = classes()
                .that().resideInAnyPackage(VALUE_OBJECT)
                .and().areNotAnnotatedWith(ValueObjectFactory.class)
                .should().beRecords()
                .orShould().beEnums()
                .allowEmptyShould(true);

        recordRule.check(importedClasses());
    }

    protected void validateDomainEventMustBeRecord() {
        var recordRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .should().beRecords()
                .allowEmptyShould(true);

        recordRule.check(importedClasses());
    }

}

