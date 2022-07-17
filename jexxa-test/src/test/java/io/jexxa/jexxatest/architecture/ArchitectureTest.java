package io.jexxa.jexxatest.architecture;

import io.jexxa.jexxatest.architecture.validapplication.ValidApplication;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    @Test
    void validateOnionArchitecture()
    {
        var objectUnderTest = new OnionArchitecture(ValidApplication.class);
        objectUnderTest.validate();
    }

    @Test
    void validatePatternLanguage()
    {
        var objectUnderTest = new PatternLanguage(ValidApplication.class);
        objectUnderTest.validate();
    }

    @Test
    void validateAggregates()
    {
        var objectUnderTest = new StatelessApplicationCore(ValidApplication.class);
        objectUnderTest.validate();
    }

}