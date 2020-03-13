package io.ddd.Jexxa.infrastructure.drivingadapter;

import java.util.ArrayList;
import java.util.List;

public class CompositeDrivingAdapter implements IDrivingAdapter
{
    private final List<IDrivingAdapter> drivingAdapterlist = new ArrayList<>();

    @Override
    public void start()
    {
        for (IDrivingAdapter iterator : drivingAdapterlist ) {
            iterator.start();
        }
    }

    @Override
    public void stop()
    {
        for (IDrivingAdapter iterator : drivingAdapterlist ) {
            iterator.stop();
        }
    }

    public void add(IDrivingAdapter drivingAdapter) {
        drivingAdapterlist.add(drivingAdapter);
    }
}
