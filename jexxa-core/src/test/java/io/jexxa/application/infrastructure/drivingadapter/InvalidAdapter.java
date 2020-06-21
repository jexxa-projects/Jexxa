package io.jexxa.application.infrastructure.drivingadapter;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;

public final class InvalidAdapter implements IDrivingAdapter
{
    private InvalidAdapter()
    {
        //Create an invalid adapter for testing fail-fast approach
    }

    @Override
    public void start()
    {
        //Create an invalid adapter for testing fail-fast approach
    }

    @Override
    public void stop()
    {
        //Create an invalid adapter for testing fail-fast approach
    }

    @Override
    public void register(Object port)
    {
        //Create an invalid adapter for testing fail-fast approach
    }
}
