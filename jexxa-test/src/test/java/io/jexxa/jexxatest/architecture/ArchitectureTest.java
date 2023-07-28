package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.jexxatest.architecture.invalidapplication.InvalidApplication;
import io.jexxa.jexxatest.architecture.validapplication.ValidApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ArchitectureTest {

    @Test
    void validateOnionArchitecture()
    {
        var objectUnderTest = new PortsAndAdapters(ValidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS)
                .addDrivenAdapterPackage("jms")

                .addDrivingAdapterPackage("rest")
                .addDrivingAdapterPackage("jms");

        objectUnderTest.validate();
    }

    @Test
    void validatePatternLanguage()
    {
        var objectUnderTest = new PatternLanguage(ValidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);
        objectUnderTest.validate();
    }

    @Test
    void validateAggregates()
    {
        var objectUnderTest = new AggregateRules(ValidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);
        objectUnderTest.validate();
    }

    @Test
    void validateInvalidOnionArchitecture()
    {
        var objectUnderTest = new PortsAndAdapters(InvalidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS)
                .addDrivingAdapterPackage("rest");
        assertThrows(AssertionError.class, objectUnderTest::validate);
    }

    @Test
    void validateInvalidPatternLanguage()
    {
        var objectUnderTest = new PatternLanguage(InvalidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);
        assertThrows(AssertionError.class, objectUnderTest::validate);
    }

    @Test
    void validateInvalidAggregates()
    {
        var objectUnderTest = new AggregateRules(InvalidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);

        assertThrows(AssertionError.class, objectUnderTest::validateOnlyAggregatesHaveAggregatesAsFields);
        assertThrows(AssertionError.class, objectUnderTest::validateOnlyAggregatesAndNestedClassesAreMutable);
        assertThrows(AssertionError.class, objectUnderTest::validateOnlyRepositoriesAcceptAggregates);
        assertThrows(AssertionError.class, objectUnderTest::validateReturnAggregates);
        assertThrows(AssertionError.class, objectUnderTest::validateAggregateID);
        assertThrows(AssertionError.class, objectUnderTest::validate);
    }
}