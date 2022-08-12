package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.Repository;

import java.util.List;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static io.jexxa.jexxatest.architecture.PackageName.APPLICATIONSERVICE;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN_SERVICE;

public class AggregateRules extends ProjectContent {

    @SuppressWarnings("unused")
    AggregateRules(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }

    protected AggregateRules(Class<?> project, ImportOption importOption)
    {
       super(project,importOption);
    }


    public void validate()
    {
        validateOnlyAggregatesHaveAggregatesAsFields();
        validateOnlyAggregatesAndNestedClassesAreMutable();
        validateOnlyRepositoriesAcceptAggregates();
        validateReturnAggregates();
    }

    protected void validateReturnAggregates()
    {
        var invalidReturnType = noMethods().that()
                .arePublic().and()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_SERVICE, DOMAIN))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Aggregate.class)
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawReturnType(thatIsAnnotatedWithAggregate())
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be returned by an aggregate or a repository!");

        invalidReturnType.check(importedClasses());
    }

    protected void validateOnlyRepositoriesAcceptAggregates() {
        var invalidReturnType = noMethods().that()
                .arePublic().and()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN, DOMAIN_SERVICE))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawParameterTypes(thatAreAggregates())
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be accepted by a Repository!");

        invalidReturnType.check(importedClasses());
    }



    protected void validateOnlyAggregatesAndNestedClassesAreMutable() {
        var finalFields = fields().that().areDeclaredInClassesThat()
                .areNotAnnotatedWith(Aggregate.class).and()
                .areDeclaredInClassesThat().areNotNestedClasses()
                .should().beFinal()
                .allowEmptyShould(true)
                .because("Only Aggregates or nested classes are allowed to use non-final fields!");

        finalFields.check(importedClasses());
    }


    protected void validateOnlyAggregatesHaveAggregatesAsFields() {
        var invalidReturnType = noFields().that().areDeclaredInClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_SERVICE, DOMAIN)
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Aggregate.class)
                .should().haveRawType(thatIsAnnotatedWithAggregate())
                .allowEmptyShould(true)
                .because("Only aggregates can keep a reference to an aggregate!");

        invalidReturnType.check(importedClasses());
    }

    private static DescribedPredicate<List<JavaClass>> thatAreAggregates() {
        return new DescribedPredicate<>("one parameter of type is an Aggregate") {
            @Override
            public boolean apply(List<JavaClass> input) {
                return input.stream().anyMatch(element -> element.isAnnotatedWith(Aggregate.class));
            }
        };
    }

    private static DescribedPredicate<JavaClass> thatIsAnnotatedWithAggregate() {
        return new DescribedPredicate<>("one parameter of type is an Aggregate") {
            @Override
            public boolean apply(JavaClass input) {
                return input.isAnnotatedWith(Aggregate.class);
            }
        };
    }
}