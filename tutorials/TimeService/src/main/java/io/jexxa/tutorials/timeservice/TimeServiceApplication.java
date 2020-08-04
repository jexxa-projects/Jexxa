package io.jexxa.tutorials.timeservice;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.tutorials.timeservice.infrastructure.drivingadapter.messaging.PublishTimeListener;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class TimeServiceApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String JMS_DRIVEN_ADAPTER      = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.messaging";
    private static final String CONSOLE_DRIVEN_ADAPTER  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.console";
    private static final String OUTBOUND_PORTS          = TimeServiceApplication.class.getPackageName() + ".domainservice";

    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain("TimeService");

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                
                //Define the driving adapter that should which implementation of the outbound port should be used by Jexxa.
                //Note: We must only register a single driven adapter for the outbound port
                .addToInfrastructure(getDrivenAdapter(args));

                // If JMS is enabled bind 'JMSAdapter' to our application 
                // Note: Jexxa's JMSAdapter is a so called specific driving adapter which cannot be directly connected directly
                // to an inbound port because we cannot apply any convention. In this case bind Jexxa's specific driving adapter
                // 'JMSAdapter' to an application specific DrivingAdapter which is `PublishTimeListener`
                if ( isJMSEnabled(args) )
                {
                    jexxaMain.bind(JMSAdapter.class).to(PublishTimeListener.class);
                }


        //The rest of main is similar to tutorial HelloJexxa
        jexxaMain
                // Bind RESTfulRPCAdapter and JMXAdapter to TimeService class so that we can invoke its method
                .bind(RESTfulRPCAdapter.class).to(TimeService.class)
                .bind(JMXAdapter.class).to(TimeService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }

    private static String getDrivenAdapter(String[] args)
    {
        if ( isJMSEnabled(args) )
        {
            return JMS_DRIVEN_ADAPTER;
        }

        return CONSOLE_DRIVEN_ADAPTER;
    }

    private static boolean isJMSEnabled(String[] args)
    {
        Options options = new Options();
        options.addOption("j", "jms", false, "jms driven adapter");

        CommandLineParser parser = new DefaultParser();
        try
        {
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("jms"))
            {
                return true;
            }
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(TimeServiceApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return false;
    }

    private TimeServiceApplication()
    {
        //Private constructor since we only offer main 
    }
}
