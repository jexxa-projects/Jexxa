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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class TimeServiceApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = TimeServiceApplication.class.getPackageName() + ".domainservice";
    private static String[] args;

    public static void main(String[] args)
    {
        //Store the arguments for internal use
        TimeServiceApplication.args = args;

        // Define the default strategy for messaging which is either a simple logger called `MessageLogger.class` or `JMSSender.class` for JMS messages
        MessageSenderManager.getInstance().setDefaultStrategy(getMessageSenderStrategy());

        //Create your jexxaMain for this application
        JexxaMain jexxaMain = new JexxaMain("TimeService");

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)

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

    private static Class<? extends MessageSender> getMessageSenderStrategy()
    {
        if ( isJMSEnabled() )
        {
            return JMSSender.class;
        }

        return MessageLogger.class;
    }

    private static boolean isJMSEnabled()
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
