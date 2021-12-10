package io.jexxa.tutorials.bookstore.applicationservice;


import io.jexxa.core.JexxaMain;
import io.jexxa.jexxatest.JexxaTest;
import io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorder;
import io.jexxa.tutorials.bookstore.BookStoreApplication;
import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut.bookSoldOut;
import static org.junit.jupiter.api.Assertions.*;

class BookStoreServiceTest
{
    private static final String DRIVEN_ADAPTER = BookStoreApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String DOMAIN_SERVICE = BookStoreApplication.class.getPackageName() + ".domainservice";
    private static final String APPLICATION_SERVICE = BookStoreApplication.class.getPackageName() + ".applicationservice";

    private static final ISBN13 ISBN_13 = new ISBN13( "978-3-86490-387-8" );
    private static JexxaMain jexxaMain;
    private BookStoreService objectUnderTest;

    private MessageRecorder publishedDomainEvents;
    private IBookRepository bookRepository;


    @BeforeAll
    static void initBeforeAll()
    {
        // We recommend instantiating JexxaMain only once for each test class.
        // If you have larger tests this speeds up Jexxa's dependency injection
        // Note: For unit-tests you just need to bind any driving adapter
        jexxaMain = new JexxaMain(BookStoreServiceTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER)
                .addToApplicationCore(APPLICATION_SERVICE)
                .addToApplicationCore(DOMAIN_SERVICE);
    }

    @BeforeEach
    void initTest()
    {
        // JexxaTest is created for each test. It provides and cleans up stubs before each test
        // Actually, JexxaTest provides stubs for repositories and send messages
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        // Query a message recorder for an interface which is defines in your application core.
        publishedDomainEvents = jexxaTest.getMessageRecorder(IDomainEventPublisher.class);
        // Query the repository that is internally used.
        bookRepository = jexxaTest.getRepository(IBookRepository.class);
        // Query the application service we want to test.
        objectUnderTest = jexxaTest.getInstanceOfPort(BookStoreService.class);
    }

    @Test
    void receiveBook()
    {
        //Arrange
        var amount = 5;

        //Act
        objectUnderTest.addToStock(ISBN_13.getValue(), amount);

        //Assert - Here you can also use all the interfaces for driven adapters defined in your application without running the infrastructure
        assertEquals( amount, objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( amount, bookRepository.get( ISBN_13 ).amountInStock() );
        assertTrue( publishedDomainEvents.isEmpty() );
    }


    @Test
    void sellBook() throws BookNotInStockException
    {
        //Arrange
        var amount = 5;
        objectUnderTest.addToStock(ISBN_13.getValue(), amount);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert - Here you can also use all the interfaces for driven adapters defined in your application without running the infrastructure
        assertEquals( amount - 1, objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( amount - 1, bookRepository.get(ISBN_13).amountInStock() );
        assertTrue( publishedDomainEvents.isEmpty() );
    }

    @Test
    void sellBookNotInStock()
    {
        //Arrange - Nothing

        //Act/Assert
        assertThrows(BookNotInStockException.class, () -> objectUnderTest.sell(ISBN_13));
    }

    @Test
    void sellLastBook() throws BookNotInStockException
    {
        //Arrange
        objectUnderTest.addToStock(ISBN_13.getValue(), 1);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert - Here you can also use all the interfaces for driven adapters defined in your application without running the infrastructure
        assertEquals( 0 , objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( 1 , publishedDomainEvents.size() );
        assertEquals( bookSoldOut(ISBN_13), publishedDomainEvents.getMessage(BookSoldOut.class));
    }

}