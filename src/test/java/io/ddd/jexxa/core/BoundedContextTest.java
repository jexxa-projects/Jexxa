package io.ddd.jexxa.core;


import javax.sound.midi.SysexMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class BoundedContextTest
{
    final JexxaMain jexxaMain = new JexxaMain("BoundedContextTest");

    @Test
    @Timeout(2)
    public void runAndShutdown()
    {
        //Arrange
        var thread = new Thread(this::invokeShutdown);
        thread.start();

        //Act
        jexxaMain.start().waitForShutdown();
    }

    void invokeShutdown()
    {
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (!jexxaMain.getBoundedContext().isRunning())
        {
           Thread.onSpinWait();
        }

        jexxaMain.getBoundedContext().shutdown();
    }
}
