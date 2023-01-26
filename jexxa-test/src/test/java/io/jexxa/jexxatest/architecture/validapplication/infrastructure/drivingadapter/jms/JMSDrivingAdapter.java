package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivingadapter.jms;


import io.jexxa.addend.infrastructure.DrivingAdapter;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidValueObject;

@DrivingAdapter
@SuppressWarnings("unused")
public class JMSDrivingAdapter
{
    JMSDrivingAdapter(){
        ValidValueObject validValueObject= new ValidValueObject(42);
    }
}
