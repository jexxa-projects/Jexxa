package io.jexxa.tutorials.bookstore;


import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.bookstore.applicationservice.BookStoreService;
import io.jexxa.tutorials.bookstore.domainservice.ReferenceLibrary;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class BookStoreApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = BookStoreApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = BookStoreApplication.class.getPackageName() + ".domainservice";

    public static void main(String[] args)
    {
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.getInstance().setDefaultStrategy(getDrivenAdapterStrategy(args));

        JexxaMain jexxaMain = new JexxaMain(BookStoreApplication.class.getSimpleName());

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)

                //Get the latest books when starting the application
                .bootstrap(ReferenceLibrary.class).with(ReferenceLibrary::addLatestBooks)

                .bind(RESTfulRPCAdapter.class).to(BookStoreService.class)
                .bind(JMXAdapter.class).to(BookStoreService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())

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
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("jdbc"))
            {
                JexxaLogger.getLogger(BookStoreApplication.class).info("Use persistence strategy: {} ", JDBCKeyValueRepository.class.getSimpleName());
                return JDBCKeyValueRepository.class;
            }
            else
            {
                JexxaLogger.getLogger(BookStoreApplication.class).info("Use persistence strategy: {} ", IMDBRepository.class.getSimpleName());
                return IMDBRepository.class;
            }
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(BookStoreApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return IMDBRepository.class;
    }


    private BookStoreApplication()
    {
        //Private constructor since we only offer main
    }

    
}
