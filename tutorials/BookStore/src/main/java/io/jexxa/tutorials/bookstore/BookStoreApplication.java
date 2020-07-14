package io.jexxa.tutorials.bookstore;


import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.bookstore.applicationservice.BookStoreService;

public class BookStoreApplication
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = BookStoreApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = BookStoreApplication.class.getPackageName() + ".domainservice";

    public static void main(String[] args)
    {
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);

        JexxaMain jexxaMain = new JexxaMain(BookStoreApplication.class.getSimpleName());

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addToApplicationCore(OUTBOUND_PORTS)

                //Define which driven adapter should be used by Jexxa
                //Note: We can only register one driven adapter for the
                .addToInfrastructure(DRIVEN_ADAPTER)

                // Bind a REST and JMX adapter to the TimeService
                // It allows to access the public methods of the TimeService via RMI over REST or Jconsole
                .bind(RESTfulRPCAdapter.class).to(BookStoreService.class)
                .bind(JMXAdapter.class).to(BookStoreService.class)

                .bind(JMXAdapter.class).to(jexxaMain.getBoundedContext())
                .bind(RESTfulRPCAdapter.class).to(jexxaMain.getBoundedContext())

                .start()

                .waitForShutdown()

                .stop();
    }


    private BookStoreApplication()
    {
        //Private constructor since we only offer main
    }

    
}
