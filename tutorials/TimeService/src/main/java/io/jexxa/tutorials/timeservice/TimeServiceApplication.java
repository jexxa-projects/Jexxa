package io.jexxa.tutorials.timeservice;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging.MessageLogger;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.tutorials.timeservice.infrastructure.drivingadapter.messaging.PublishTimeListener;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.*;

public final class TimeServiceApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String DRIVING_ADAPTER = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivingadapter";
    private static final String OUTBOUND_PORTS  = TimeServiceApplication.class.getPackageName() + ".domainservice";

    public static void main(String[] args)
    {
        // Define the default strategy for messaging which is either a simple logger called `MessageLogger.class` or `JMSSender.class` for JMS messages
        MessageSenderManager.setDefaultStrategy(getMessagingStrategy(args));

        //Create your jexxaMain for this application
        var jexxaMain = new JexxaMain("TimeService");

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)
                //Note: Since we provide our own special driving adapters, we have to add it to the infrastructure
                .addToInfrastructure(DRIVING_ADAPTER)

                // Bind RESTfulRPCAdapter and JMXAdapter to TimeService class so that we can invoke its method
                .bind(RESTfulRPCAdapter.class).to(TimeService.class)
                .bind(JMXAdapter.class).to(TimeService.class)


                // Conditional bind is only executed if given expression evaluates to true
                .conditionalBind( TimeServiceApplication::isJMSEnabled, JMSAdapter.class).to(PublishTimeListener.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }

    // Methods for command line parsing
    static Options getOptions()
    {
        var options = new Options();
        options.addOption("j", "jdbc", false, "jdbc driven adapter strategy");
        options.addOption("J", "jms", false, "JMS message sender");
        return options;
    }

    static boolean isJMSEnabled()
    {
        return MessageSenderManager.getDefaultStrategy() == JMSSender.class;
    }


    private static Class<? extends MessageSender> getMessagingStrategy(String[] args)
    {
        if (parameterAvailable("jms", args))
        {
            JexxaLogger.getLogger(TimeServiceApplication.class).info("Use messaging strategy: {} ", JMSSender.class.getSimpleName());
            return JMSSender.class;
        }

        JexxaLogger.getLogger(TimeServiceApplication.class).info("Use messaging strategy: {} ", MessageLogger.class.getSimpleName());
        return MessageLogger.class;
    }

    @SuppressWarnings("SameParameterValue")
    static boolean parameterAvailable(String parameter, String[] args)
    {
        CommandLineParser parser = new DefaultParser();
        try
        {
            CommandLine line = parser.parse( getOptions(), args );

            return line.hasOption(parameter);
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
