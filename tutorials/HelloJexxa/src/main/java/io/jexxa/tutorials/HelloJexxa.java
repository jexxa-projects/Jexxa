package io.jexxa.tutorials;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public final class HelloJexxa
{
    @SuppressWarnings({"java:S3400", "unused"})
    public String greetings()
    {
        return "Hello Jexxa";
    }

    public static void main(String[] args)
    {
        //Create your jexxaMain for this application
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa");

        jexxaMain
                // Bind a JMX adapter to our BoundedContext object.
                // It allows to access the public methods of the object via `jconsole`
                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                // Bind a REST adapter to a HelloJexxa object
                .bind(RESTfulRPCAdapter.class).to(HelloJexxa.class)

                //Start Jexxa and all bindings
                // - Open following URL in browser to get greetings: http://localhost:7000/HelloJexxa/greetings
                // - You can also use curl: `curl -X GET http://localhost:7000/HelloJexxa/greetings`
                .start()

                //Wait until shutdown is called by one of the following options:
                // - Press CTRL-C
                // - Use `jconsole` to connect to this application and invoke method shutdown
                .waitForShutdown()

                //Finally invoke stop() for proper cleanup
                .stop();
    }
}
