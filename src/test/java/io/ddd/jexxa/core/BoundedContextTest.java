package io.ddd.jexxa.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class BoundedContextTest
{
    final JexxaMain jexxaMain = new JexxaMain("BoundedContextTest");
    BoundedContext objectUnderTest;

    @Test
    @Timeout(1)
    public void runAndShutdown()
    {
        //Arrange
        objectUnderTest = jexxaMain.start();
        var thread = new Thread(this::invokeShutdown);
        thread.start();

        //Act
        objectUnderTest.waitForShutdown();
    }

    void invokeShutdown()
    {
        while (!objectUnderTest.isRunning())
        {
           Thread.onSpinWait();
        }

        objectUnderTest.shutdown();
    }
}
