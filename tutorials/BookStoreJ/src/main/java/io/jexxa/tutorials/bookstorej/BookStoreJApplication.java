package io.jexxa.tutorials.bookstorej;


import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging.MessageLogger;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.bookstorej.domainservice.ReferenceLibrary;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class BookStoreJApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = BookStoreJApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = BookStoreJApplication.class.getPackageName() + ".domainservice";
    //Add also package name with inbound ports so that they are scanned by Jexxa
    private static final String INBOUND_PORTS   = BookStoreJApplication.class.getPackageName() + ".applicationservice";

    public static void main(String[] args)
    {
        // Define the default strategy which is either an IMDB database or a JDBC based repository
        // In case of JDBC we use a simple key value approach which stores the key and the value as json strings.
        // Using json strings might be very inconvenient if you come from typical relational databases but in terms
        // of DDD our aggregate is responsible to ensure consistency of our data and not the database.
        RepositoryManager.setDefaultStrategy(getDrivenAdapterStrategy(args));
        MessageSenderManager.setDefaultStrategy(MessageLogger.class);

        JexxaMain jexxaMain = new JexxaMain(BookStoreJApplication.class.getSimpleName());

        jexxaMain
                // In order to find ports by annotation we must add packages that are searched by Jexxa.
                // Therefore, we must also add inbound ports to application core
                .addToApplicationCore(INBOUND_PORTS)
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER)

                //Get the latest books when starting the application
                .bootstrap(ReferenceLibrary.class).with(ReferenceLibrary::addLatestBooks)

                // In case you annotate your domain core with your pattern language,
                // You can also bind DrivingAdapter to annotated classes.
                .bind(RESTfulRPCAdapter.class).toAnnotation(ApplicationService.class)
                .bind(JMXAdapter.class).toAnnotation(ApplicationService.class)

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
                JexxaLogger.getLogger(BookStoreJApplication.class).info("Use persistence strategy: {} ", JDBCKeyValueRepository.class.getSimpleName());
                return JDBCKeyValueRepository.class;
            }
            else
            {
                JexxaLogger.getLogger(BookStoreJApplication.class).info("Use persistence strategy: {} ", IMDBRepository.class.getSimpleName());
                return IMDBRepository.class;
            }
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(BookStoreJApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return IMDBRepository.class;
    }


    private BookStoreJApplication()
    {
        //Private constructor since we only offer main
    }


}
