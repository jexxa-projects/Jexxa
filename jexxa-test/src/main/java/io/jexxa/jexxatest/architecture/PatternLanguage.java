package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
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

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATION;
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

    @Override
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
                .that().resideInAnyPackage(APPLICATION, APPLICATIONSERVICE)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(ApplicationService.class)
                .orShould().beAnnotatedWith(InfrastructureService.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateDomainService()
    {
        var annotationRule = classes().that().resideInAnyPackage(DOMAIN_SERVICE)
                .and().areNotAnonymousClasses()
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

    protected void validateDomainEventMustBeRecord() {
         classes()
                .that().areAnnotatedWith(DomainEvent.class)
                .should(beRecordsOrValidInterfacesWithAnnotatedRecords())
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    private ArchCondition<JavaClass> beRecordsOrValidInterfacesWithAnnotatedRecords() {
        return new ArchCondition<>("be records or interfaces whose nested records are also annotated with @DomainEvent") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (javaClass.isInterface()) {
                    validateInterfaceContainer(javaClass, events);
                } else {
                    validateClassIsRecord(javaClass, events);
                }
            }
        };
    }

    private void validateInterfaceContainer(JavaClass javaClass, ConditionEvents events) {
        List<Class<?>> nestedRecords = Arrays.stream(javaClass.reflect().getDeclaredClasses())
                .filter(Class::isRecord)
                .toList();

        boolean hasNoRecords = nestedRecords.isEmpty();
        boolean hasUnannotatedRecords = nestedRecords.stream()
                .anyMatch(foundRecord -> !foundRecord.isAnnotationPresent(DomainEvent.class));

        if (hasNoRecords || hasUnannotatedRecords) {
            String message = String.format(
                    "Interface %s carries @DomainEvent, but is invalid: All nested records inside its body must also be explicitly annotated with @DomainEvent!",
                    javaClass.getName()
            );
            events.add(SimpleConditionEvent.violated(javaClass, message));
        }
    }

    private void validateClassIsRecord(JavaClass javaClass, ConditionEvents events) {
        if (!javaClass.isRecord()) {
            String message = String.format("Class %s is annotated with @DomainEvent but is NOT a record!", javaClass.getName());
            events.add(SimpleConditionEvent.violated(javaClass, message));
        }
    }

}

