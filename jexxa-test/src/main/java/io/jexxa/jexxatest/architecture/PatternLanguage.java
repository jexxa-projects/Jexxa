package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateFactory;
import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.addend.applicationcore.BusinessException;
import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.addend.applicationcore.DomainService;
import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.addend.applicationcore.Observer;
import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.addend.applicationcore.ValueObject;
import io.jexxa.addend.applicationcore.ValueObjectFactory;
import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.addend.infrastructure.DrivingAdapter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVEN_ADAPTER;
import static io.jexxa.jexxatest.architecture.PackageName.DRIVING_ADAPTER;

public class PatternLanguage extends ProjectContent {

    @SuppressWarnings("unused")
    PatternLanguage(Class<?> project)
    {
        this(project,ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }
    protected PatternLanguage(Class<?> project, ImportOption importOption)
    {
        super(project,importOption);
    }

    public void validate()
    {
        validateApplicationService();
        validateDomainService();
        validateDomain();

        validateDrivenAdapter();
        validateDrivingAdapter();

        validateRepositoryMustBeInterfaces();
        validateInfrastructureServiceMustBeInterfaces();
        validateValueObjectMustBeRecords();
        validateDomainEventMustBeRecord();
    }


    protected void validateApplicationService()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(APPLICATIONSERVICE)
                .should().beAnnotatedWith(ApplicationService.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateDomainService()
    {
        var annotationRule = classes().that().resideInAnyPackage(DOMAIN_SERVICE)
                .should().beAnnotatedWith(InfrastructureService.class)
                .orShould().beAnnotatedWith(DomainService.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateDomain()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(Aggregate.class)
                .orShould().beAnnotatedWith(DomainEvent.class)
                .orShould().beAnnotatedWith(ValueObject.class)
                .orShould().beAnnotatedWith(BusinessException.class)
                .orShould().beAnnotatedWith(Repository.class)
                .orShould().beAnnotatedWith(ValueObjectFactory.class)
                .orShould().beAnnotatedWith(AggregateFactory.class)
                .orShould().beAnnotatedWith(Observer.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateDrivenAdapter()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(DRIVEN_ADAPTER)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().areNotEnums()
                .should().beAnnotatedWith(DrivenAdapter.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateDrivingAdapter()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(DRIVING_ADAPTER)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().areNotEnums()
                .should().beAnnotatedWith(DrivingAdapter.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateRepositoryMustBeInterfaces()
    {
        var interfaceRule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        interfaceRule.check(importedClasses());
    }

    protected void validateInfrastructureServiceMustBeInterfaces()
    {
        var interfaceRule = classes()
                .that().areAnnotatedWith(InfrastructureService.class)
                .should().beInterfaces()
                .allowEmptyShould(true);

        interfaceRule.check(importedClasses());
    }

    protected void validateValueObjectMustBeRecords()
    {
        var recordRule = classes()
                .that().areAnnotatedWith(ValueObject.class)
                .should().beRecords()
                .orShould().beEnums()
                .allowEmptyShould(true);

        recordRule.check(importedClasses());
    }

    protected void validateDomainEventMustBeRecord()
    {
        var recordRule = classes()
                .that().areAnnotatedWith(DomainEvent.class)
                .should().beRecords()
                .allowEmptyShould(true);

        recordRule.check(importedClasses());
    }

}

