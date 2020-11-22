package io.jexxa.tutorials;


import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging.MessageLogger;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.applicationservice.SimpleDomainEventStore;
import io.jexxa.tutorials.infrastructure.drivingadapter.messaging.BookStoreListener;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class DomainEventStoreApplication
{
    private static final String DRIVEN_ADAPTER = DomainEventStoreApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String DRIVING_ADAPTER = DomainEventStoreApplication.class.getPackageName() + ".infrastructure.drivingadapter";
    private static final String OUTBOUND_PORTS = DomainEventStoreApplication.class.getPackageName() + ".domainservice";
    //Add also package name with inbound ports so that they are scanned by Jexxa
    private static final String INBOUND_PORTS = DomainEventStoreApplication.class.getPackageName() + ".applicationservice";

    public static void main(String[] args)
    {
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.setDefaultStrategy(getRepositoryStrategy(args));
        // The message sender is either a simple MessageLogger or a JMS sender.
        MessageSenderManager.setDefaultStrategy(getMessagingStrategy(args));

        JexxaMain jexxaMain = new JexxaMain(DomainEventStoreApplication.class.getSimpleName());

        jexxaMain
                // In order to find ports by annotation we must add packages that are searched by Jexxa.
                // Therefore, we must also add inbound ports to application core
                .addToApplicationCore(INBOUND_PORTS)
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)
                .addToInfrastructure(DRIVING_ADAPTER)


                // In case you annotate your domain core with your pattern language,
                // You can also bind DrivingAdapter to annotated classes.
                .bind(RESTfulRPCAdapter.class).to(SimpleDomainEventStore.class)

                .bind(JMSAdapter.class).to(BookStoreListener.class)

                .start()

                .waitForShutdown()

                .stop();
    }


    // Methods for command line parsing
    static Options getOptions()
    {
        Options options = new Options();
        options.addOption("j", "jdbc", false, "jdbc driven adapter strategy");
        options.addOption("J", "jms", false, "JMS message sender");
        return options;
    }


    @SuppressWarnings("rawtypes")
    private static Class<? extends IRepository> getRepositoryStrategy(String[] args)
    {
        if (parameterAvailable("jdbc", args))
        {
            JexxaLogger.getLogger(DomainEventStoreApplication.class).info("Use persistence strategy: {} ", JDBCKeyValueRepository.class.getSimpleName());
            return JDBCKeyValueRepository.class;
        }

        JexxaLogger.getLogger(DomainEventStoreApplication.class).info("Use persistence strategy: {} ", IMDBRepository.class.getSimpleName());
        return IMDBRepository.class;
    }

    private static Class<? extends MessageSender> getMessagingStrategy(String[] args)
    {
        if (parameterAvailable("jms", args))
        {
            JexxaLogger.getLogger(DomainEventStoreApplication.class).info("Use messaging strategy: {} ", JMSSender.class.getSimpleName());
            return JMSSender.class;
        }

        JexxaLogger.getLogger(DomainEventStoreApplication.class).info("Use messaging strategy: {} ", MessageLogger.class.getSimpleName());
        return MessageLogger.class;
    }

    static boolean parameterAvailable(String parameter, String[] args)
    {
        CommandLineParser parser = new DefaultParser();
        try
        {
            CommandLine line = parser.parse( getOptions(), args );

            return line.hasOption(parameter);
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(DomainEventStoreApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return false;
    }

    private DomainEventStoreApplication()
    {
        // Private constructor
    }
}