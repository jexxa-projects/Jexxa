package io.jexxa.application.applicationservice;

import java.util.ArrayList;
import java.util.List;

public class IncrementApplicationService
{
    private int counter = 0;
    private int maxCounter = 1000; //some meaningful value
    private final List<Integer> usedCounter = new ArrayList<>();

    @SuppressWarnings("unused")
    public void increment()
    {
        if ( counter < maxCounter )
        {
            ++counter;
            usedCounter.add(counter);
        }
    }

    public void setMaxCounter(int maxCounter)
    {
        this.maxCounter = maxCounter;
    }

    public int getCounter()
    {
        return counter;
    }

    public List<Integer> getUsedCounter()
    {
        return usedCounter;
    }
}
