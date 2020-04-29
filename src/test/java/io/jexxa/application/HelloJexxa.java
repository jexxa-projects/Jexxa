package io.jexxa.application;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        //Create your jexxaMain for this application
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa");

        jexxaMain
                //Connect a JMX adapter to an object in order to access its public methods via `jconsole`
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                //Connect a REST adapter to an object in order to access its public methods via RMI over REST
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                //Start Jexxa and establish all connections
                .start()

                //Wait until shutdown is called by one of the following options:
                // - Press CTRL-C
                // - Use `jconsole` to connect to this application and invoke method shutdown
                // - Use HTTP-post to URL: `http://localhost:7000/BoundedContext/shutdown`
                //   (using curl: `curl -X POST http://localhost:7000/BoundedContext/shutdown`)
                .waitForShutdown()

                //Finally invoke stop() for proper cleanup
                .stop();
    }
}


