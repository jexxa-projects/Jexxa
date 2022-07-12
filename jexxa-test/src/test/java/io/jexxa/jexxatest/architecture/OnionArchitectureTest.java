package io.jexxa.jexxatest.architecture;

import io.jexxa.jexxatest.architecture.jexxaapplication.JexxaApplication;
import org.junit.jupiter.api.Test;

class OnionArchitectureTest {

    @Test
    void validate()
    {
        var objectUnderTest = new PatternLanguage(JexxaApplication.class);
        objectUnderTest.validate();
    }
}