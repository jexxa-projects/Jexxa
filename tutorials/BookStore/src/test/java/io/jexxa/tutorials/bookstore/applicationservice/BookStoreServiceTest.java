package io.jexxa.tutorials.bookstore.applicationservice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jexxa.core.JexxaMain;
import io.jexxa.test.JexxaTest;
import io.jexxa.test.infrastructure.drivenadapterstrategy.messaging.recording.MessageRecorder;
import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookStoreServiceTest
{
    private static final String DRIVEN_ADAPTER = "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter";
    private static final String DOMAIN_SERVICE = "io.jexxa.tutorials.bookstore.domainservice";

    private static final String ISBN_13 = "978-3-86490-387-8";
    private static JexxaMain jexxaMain;
    private JexxaTest jexxaTest;
    private BookStoreService objectUnderTest;

    private MessageRecorder publishedDomainEvents;


    @BeforeAll
    static void initBeforeAll()
    {
        jexxaMain = new JexxaMain(BookStoreServiceTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER)
                .addToApplicationCore(DOMAIN_SERVICE);
    }

    @BeforeEach
    void initTest()
    {
        jexxaTest = new JexxaTest(jexxaMain);
        publishedDomainEvents = jexxaTest.getMessageRecorder(IDomainEventPublisher.class);
        objectUnderTest = jexxaTest.getInstanceOfPort(BookStoreService.class);
    }

    @Test
    void receiveBook()
    {
        //Arrange
        var amount = 5;

        //Act
        objectUnderTest.addToStock(ISBN_13, amount);

        //Assert
        assertEquals( amount, objectUnderTest.amountInStock(ISBN_13) );
        assertTrue( publishedDomainEvents.isEmpty() );
    }


    @Test
    void sellBook() throws BookNotInStockException
    {
        //Arrange
        var amount = 5;
        objectUnderTest.addToStock(ISBN_13, amount);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert
        assertEquals( amount - 1, objectUnderTest.amountInStock(ISBN_13) );
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
        objectUnderTest.addToStock(ISBN_13, 1);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert
        assertEquals( 0 , objectUnderTest.amountInStock(ISBN_13) );
        assertEquals( 1 , publishedDomainEvents.size() );
    }

}