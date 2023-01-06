package io.jexxa.jexxatest;

import io.jexxa.jexxatest.application.JexxaITTestApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

import static io.jexxa.jexxatest.JexxaTest.loadJexxaTestProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JexxaIntegrationTestIT {
    private static JexxaIntegrationTest jexxaIntegrationTest;

    @BeforeAll
    static void initBeforeAll()
    {
        //Start the application
        var result = Executors.newSingleThreadExecutor();
        result.execute(JexxaIntegrationTestIT::runApplication);

        //Connect the integration test to the application
        jexxaIntegrationTest = new JexxaIntegrationTest(JexxaITTestApplication.class);
    }

    @Test
    void testBoundedContextHandler()
    {
        //Arrange
        var boundedContext = jexxaIntegrationTest.getBoundedContext();

        //Act / Assert
        assertTrue( boundedContext.isRunning() );
        assertTrue( boundedContext.isHealthy() );

        assertNotNull( boundedContext.contextVersion() );
        assertNotNull( boundedContext.jexxaVersion() );
        assertNotNull( boundedContext.uptime() );
        assertNotNull( boundedContext.diagnostics() );

        assertEquals(JexxaITTestApplication.class.getSimpleName(), boundedContext.contextName() );
    }

    static void runApplication()
    {
        JexxaITTestApplication.main(loadJexxaTestProperties());
    }


    @AfterAll
    static void tearDown()
    {
        JexxaITTestApplication.shutDown();
        jexxaIntegrationTest.shutDown();
    }

}
