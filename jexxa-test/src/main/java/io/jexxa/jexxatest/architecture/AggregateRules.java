package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateID;
import io.jexxa.addend.applicationcore.Repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static io.jexxa.jexxatest.architecture.AggregateRules.GenericWithAggregate.returnGenericWithAggregate;
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
        validateAggregateID();
    }

    protected void validateReturnAggregates()
    {
        var aggregates = importedClasses().stream()
                .filter( element -> element.isAnnotatedWith(Aggregate.class)).map(JavaClass::reflect)
                .map(Class::getTypeName)
                .collect(Collectors.toSet());

        var invalidReturnType = noMethods().that()
                .arePublic().and()
                .areDeclaredInClassesThat(resideInAnyPackage(APPLICATIONSERVICE, DOMAIN_SERVICE, DOMAIN))
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Aggregate.class)
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(Repository.class)
                .should().haveRawReturnType(thatIsAnnotatedWithAggregate())
                .orShould(returnGenericWithAggregate(aggregates))
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

    protected void validateAggregateID(){
        var aggregateID = classes().that().areAnnotatedWith(Aggregate.class)
                .should(have(describe("Annotation @AggregateID" , javaClass ->
                        javaClass.getMethods().stream().filter(thatIsAnnotatedWithAggregateID()).count() == 1
                )))
                .allowEmptyShould(true)
                .because("Aggregate must provide a single public method that is annotated with AggregateID");

        aggregateID.check(importedClasses());

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
                return javaClass.isAnnotatedWith(Aggregate.class) ;
            }
        };
    }

    private static DescribedPredicate<JavaMethod> thatIsAnnotatedWithAggregateID() {
        return new DescribedPredicate<>("Aggregate has no method that is annotated with AggregateID") {
            @Override
            public boolean test(JavaMethod javaMethod) {
                return javaMethod.isAnnotatedWith(AggregateID.class);
            }
        };
    }

    static class GenericWithAggregate extends ArchCondition<JavaMethod>
    {
        private final Set<String> aggregates;
        public GenericWithAggregate(Set<String> aggregates) {
            super("Returns a generic type including an Aggregate", (Object) null);
            this.aggregates = aggregates;
        }
        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var genericReturnType = item.reflect().getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                for (int i = 0; i < parameterizedType.getActualTypeArguments().length; ++i) {
                    if (aggregates.contains(parameterizedType.getActualTypeArguments()[0].getTypeName())) {
                        events.add(new SimpleConditionEvent(item, true, "Returns a generic type including an Aggregate"));
                    }
                }
            }
        }

        public static GenericWithAggregate returnGenericWithAggregate(Set<String> aggregates)
        {
            return new GenericWithAggregate(aggregates);
        }
    }
}