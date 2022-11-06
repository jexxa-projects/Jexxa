package io.jexxa.adapterapi;

public class CountingObject {
    private int counter;

    public void increment()
    {
        ++counter;
    }

    public int getCounter()
    {
        return counter;
    }

    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    public int setGetCounter(int counter)
    {
        this.counter = counter;
        return this.counter;
    }

}
