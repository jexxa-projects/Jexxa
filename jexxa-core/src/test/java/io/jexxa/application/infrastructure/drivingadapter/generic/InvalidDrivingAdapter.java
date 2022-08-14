package io.jexxa.application.infrastructure.drivingadapter.generic;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;

/**
 * This DrivingAdapter is invalid because it only provides a private constructor.
 * So Jexxa cannot create an instance of this class because it does not fulfill its convention.
 */
public final class InvalidDrivingAdapter implements IDrivingAdapter
{
    private InvalidDrivingAdapter()
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
