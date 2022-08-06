package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
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

public class AggregateRules extends ArchitectureRule {

    @SuppressWarnings("unused")
    public AggregateRules(Class<?> project)
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
        validateOnlyAggregatesAndNestedClassesHaveNonFinalFields();
        validateOnlyRepositoriesAcceptAggregates();
        validateOnlyRepositoriesReturnAggregates();
    }

    protected void validateOnlyRepositoriesReturnAggregates() {
        var invalidReturnType = noMethods().that()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, DOMAIN_SERVICE))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawReturnType(thatIsAnnotatedWithAggregate())
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be returned by a Repository!");

        invalidReturnType.check(importedClasses());
    }

    protected void validateOnlyRepositoriesAcceptAggregates() {
        var invalidReturnType = noMethods().that()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, DOMAIN_SERVICE))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawParameterTypes(thatAreAggregates())
                .allowEmptyShould(true)
                .because("Aggregates contain the business logic and can only be returned by a Repository!");

        invalidReturnType.check(importedClasses());
    }



    protected void validateOnlyAggregatesAndNestedClassesHaveNonFinalFields() {
        var finalFields = fields().that().areDeclaredInClassesThat()
                .areNotAnnotatedWith(Aggregate.class)
                .or().areDeclaredInClassesThat().areNotNestedClasses()
                .should().beFinal()
                .allowEmptyShould(true)
                .because("Only Aggregates or nested classes are allowed to use non-final fields!");

        finalFields.check(importedClasses());
    }


    protected void validateOnlyAggregatesHaveAggregatesAsFields() {
        var invalidReturnType = noFields().that().areDeclaredInClassesThat()
                .resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_WORKFLOW, BUSINESS_EXCEPTION, DOMAIN_SERVICE, VALUE_OBJECT)
                .should().haveRawType(resideInAPackage(AGGREGATE))
                .allowEmptyShould(true)
                .because("An ApplicationService or DomainProcessService must not keep a reference to an aggregate!");

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