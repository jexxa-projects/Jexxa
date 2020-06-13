package io.jexxa.samples.simpletimeservice;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.samples.simpletimeservice.applicationservice.TimeService;

public class TimeServiceApplication
{

    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain(TimeServiceApplication.class.getSimpleName());

        jexxaMain.addToApplicationCore("io.jexxa.sample.simpletimeservice.domainservice")
                .addToInfrastructure("io.jexxa.sample.simpletimeservice.infrastructure.drivenadapter.messaging")

                .bind(RESTfulRPCAdapter.class).to(TimeService.class)
                .bind(JMXAdapter.class).to(TimeService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }
}
