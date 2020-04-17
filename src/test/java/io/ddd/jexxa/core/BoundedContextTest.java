package io.ddd.jexxa.core;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class BoundedContextTest
{
    final BoundedContext objectUnderTest = new BoundedContext("BoundedContextTest");

    @Test
    @Timeout(1)
    public void runAndShutdown()
    {
        //Arrange
        var thread = new Thread(this::invokeShutdown);
        thread.start();

        //Act
        objectUnderTest.run();
    }

    void invokeShutdown()
    {
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (!objectUnderTest.isRunning())
       {
          Thread.onSpinWait();
       }

       objectUnderTest.shutdown();
    }
}
