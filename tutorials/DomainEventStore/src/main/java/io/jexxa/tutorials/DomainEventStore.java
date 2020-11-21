package io.jexxa.tutorials;


import io.jexxa.core.JexxaMain;
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

public final class DomainEventStore
{
    private static final String DRIVEN_ADAPTER = DomainEventStore.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String DRIVING_ADAPTER = DomainEventStore.class.getPackageName() + ".infrastructure.drivingadapter";
    private static final String OUTBOUND_PORTS = DomainEventStore.class.getPackageName() + ".domainservice";
    //Add also package name with inbound ports so that they are scanned by Jexxa
    private static final String INBOUND_PORTS = DomainEventStore.class.getPackageName() + ".applicationservice";

    public static void main(String[] args)
    {
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.setDefaultStrategy(getDrivenAdapterStrategy(args));

        JexxaMain jexxaMain = new JexxaMain(DomainEventStore.class.getSimpleName());

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


    @SuppressWarnings("rawtypes")
    private static Class<? extends IRepository> getDrivenAdapterStrategy(String[] args)
    {
        Options options = new Options();
        options.addOption("j", "jdbc", false, "jdbc driven adapter strategy");

        CommandLineParser parser = new DefaultParser();
        try
        {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("jdbc"))
            {
                JexxaLogger.getLogger(DomainEventStore.class).info("Use persistence strategy: {} ",
                        JDBCKeyValueRepository.class.getSimpleName());
                return JDBCKeyValueRepository.class;
            }
            else
            {
                JexxaLogger.getLogger(DomainEventStore.class).info("Use persistence strategy: {} ", IMDBRepository.class.getSimpleName());
                return IMDBRepository.class;
            }
        }
        catch (ParseException exp)
        {
            JexxaLogger.getLogger(DomainEventStore.class)
                    .error("Parsing failed.  Reason: {}", exp.getMessage());
        }
        return IMDBRepository.class;
    }

    private DomainEventStore()
    {
        // Private constructor
    }
}