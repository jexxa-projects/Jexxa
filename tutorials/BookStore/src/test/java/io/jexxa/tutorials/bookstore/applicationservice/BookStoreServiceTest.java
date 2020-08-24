package io.jexxa.tutorials.bookstore.applicationservice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.tutorials.bookstore.domain.aggregate.Book;
import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub.DomainEventStubPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookStoreServiceTest
{
    private static final String DRIVEN_ADAPTER_PERSISTENCE = "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.persistence";
    private static final String DRIVEN_ADAPTER_MESSAGING =   "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub";

    private static final String ISBN_13 = "978-3-86490-387-8";

    private JexxaMain jexxaMain;

    @BeforeEach
    void initTest()
    {
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
        
        jexxaMain = new JexxaMain(BookStoreServiceTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER_MESSAGING)
                .addToInfrastructure(DRIVEN_ADAPTER_PERSISTENCE);

        DomainEventStubPublisher.clear();

        RepositoryManager.getInstance().getStrategy(Book.class, Book::getISBN13, jexxaMain.getProperties()).removeAll();
    }

    @Test
    void receiveBook()
    {
        //Arrange
        var objectUnderTest = jexxaMain.getInstanceOfPort(BookStoreService.class);
        var amount = 5;

        //Act
        objectUnderTest.addToStock(ISBN_13, amount);

        //Assert
        assertEquals( amount, objectUnderTest.amountInStock(ISBN_13) );
    }


    @Test
    void sellBook() throws BookNotInStockException
    {
        //Arrange
        var objectUnderTest = jexxaMain.getInstanceOfPort(BookStoreService.class);
        var amount = 5;
        objectUnderTest.addToStock(ISBN_13, amount);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert
        assertEquals( amount - 1, objectUnderTest.amountInStock(ISBN_13) );
    }

    @Test
    void sellBookNotInStock() 
    {
        //Arrange
        var objectUnderTest = jexxaMain.getInstanceOfPort(BookStoreService.class);

        //Act/Assert
        assertThrows(BookNotInStockException.class, () -> objectUnderTest.sell(ISBN_13));
    }

    @Test
    void sellLastBook() throws BookNotInStockException
    {
        //Arrange
        var objectUnderTest = jexxaMain.getInstanceOfPort(BookStoreService.class);
        objectUnderTest.addToStock(ISBN_13, 1);

        //Act
        objectUnderTest.sell(ISBN_13);

        //Assert
        assertEquals( 0 , objectUnderTest.amountInStock(ISBN_13) );
        assertEquals(1, DomainEventStubPublisher.eventCount() );
    }

}