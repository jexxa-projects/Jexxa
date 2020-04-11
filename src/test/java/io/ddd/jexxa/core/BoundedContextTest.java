package io.ddd.jexxa.core;

import org.junit.Test;

public class BoundedContextTest
{
    final BoundedContext objectUnderTest = new BoundedContext("BoundedContextTest");

    @Test(timeout = 1000)
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
