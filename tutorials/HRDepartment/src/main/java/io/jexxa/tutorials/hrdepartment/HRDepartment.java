package io.jexxa.tutorials.hrdepartment;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.hrdepartment.applicationservice.EmployeeService;

public final class HRDepartment
{
    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain(HRDepartment.class.getSimpleName());

        jexxaMain
                .addToInfrastructure("io.jexxa.tutorials.hrdepartment.infrastructure")
                .addToApplicationCore("io.jexxa.tutorials.hrdepartment.domainservice")

                .bind(RESTfulRPCAdapter.class).to(EmployeeService.class)
                .bind(JMXAdapter.class).to(EmployeeService.class)
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                .start()
                .waitForShutdown()
                .stop();
    }

    private HRDepartment()
    {
        //Private constructor since we only offer main 
    }
}
