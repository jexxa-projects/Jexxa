package io.jexxa.jexxatest.architecture.validapplication.infrastructure.drivenadapter.jms;


import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidValueObject;

@DrivenAdapter
@SuppressWarnings("unused")
public class JMSDrivenAdapter
{
    JMSDrivenAdapter(){
        ValidValueObject validValueObject= new ValidValueObject(42);
    }

}
