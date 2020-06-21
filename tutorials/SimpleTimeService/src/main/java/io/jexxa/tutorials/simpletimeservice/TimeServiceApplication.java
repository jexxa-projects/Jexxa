package io.jexxa.tutorials.simpletimeservice;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.simpletimeservice.applicationservice.TimeService;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class TimeServiceApplication
{
    private static final String JMS_DRIVEN_ADAPTER = "io.jexxa.tutorials.simpletimeservice.infrastructure.drivenadapter.messaging";
    private static final String CONSOLE_DRIVEN_ADAPTER = "io.jexxa.tutorials.simpletimeservice.infrastructure.drivenadapter.console";


    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain(TimeServiceApplication.class.getSimpleName());

        jexxaMain.addToApplicationCore("io.jexxa.tutorials.simpletimeservice.domainservice")
                .addToInfrastructure(getDrivenAdapter(args))

                .bind(RESTfulRPCAdapter.class).to(TimeService.class)
                .bind(JMXAdapter.class).to(TimeService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }

    private static String getDrivenAdapter(String[] args)
    {
        Options options = new Options();
        options.addOption("j", "jms", false, "jms driven adapter");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("jms"))
            {
                return JMS_DRIVEN_ADAPTER;
            }
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(TimeServiceApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return CONSOLE_DRIVEN_ADAPTER;
    }

    private TimeServiceApplication()
    {
        //Private constructor since we only offer main 
    }
}
