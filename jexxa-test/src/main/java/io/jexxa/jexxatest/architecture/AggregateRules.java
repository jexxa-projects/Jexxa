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

/**
 * Validates the following rules for @Aggregates:
 * <li>
 *     Only classes annotated with @Aggregate are allowed to have other aggregates as members.
 * </li>
 * <li>
 *     Only classes annotated with @Aggregate or inner classes (such as *Factories) have mutable fields.
 *     All other classes must have immutable fields.
 * </li>
 * <li>
 *     Only methods of a Repository accepts an Aggregate as method parameter.
 * </li>
 * <li>
 *     Only methods of a Repository or an Aggregate may return an Aggregate. Classes that are annotated with Application-,
 *     Domain-, or InfrastructureService are not allowed to return or expose an Aggregate.
 *  </li>
 *
 */
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
            public boolean test(List<JavaClass> javaClasses) {
                return javaClasses.stream().anyMatch(element -> element.isAnnotatedWith(Aggregate.class));
            }
        };
    }

    private static DescribedPredicate<JavaClass> thatIsAnnotatedWithAggregate() {
        return new DescribedPredicate<>("one parameter of type is an Aggregate") {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.isAnnotatedWith(Aggregate.class);
            }
        };
    }
}