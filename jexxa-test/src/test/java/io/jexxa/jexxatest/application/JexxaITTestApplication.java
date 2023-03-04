package io.jexxa.jexxatest.application;

import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.drivingadapter.rest.RESTfulRPCAdapter;

import java.util.Properties;

public class JexxaITTestApplication
{
    private static JexxaMain jexxaMain;
    public static void main(Properties properties)
    {
        jexxaMain = new JexxaMain(JexxaITTestApplication.class, properties);

        jexxaMain
                .bind(RESTfulRPCAdapter.class).to(SimpleApplicationService.class)
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                .run();
    }

    public static void shutDown()
    {
        if (jexxaMain != null)
        {
            jexxaMain.stop();
            jexxaMain = null;
        }
    }

    private JexxaITTestApplication()
    {
        //Private constructor since we only offer main
    }
}
