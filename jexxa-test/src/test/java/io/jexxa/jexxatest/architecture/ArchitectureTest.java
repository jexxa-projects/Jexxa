package io.jexxa.jexxatest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.jexxatest.architecture.validapplication.ValidApplication;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    @Test
    void validateOnionArchitecture()
    {
        var objectUnderTest = new OnionArchitecture(ValidApplication.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);
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

}