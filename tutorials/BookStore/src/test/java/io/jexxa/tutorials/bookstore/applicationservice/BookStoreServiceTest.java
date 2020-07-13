package io.jexxa.tutorials.bookstore.applicationservice;


import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.tutorials.bookstore.domain.businessexception.InvalidISBNException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookStoreServiceTest
{
    static private final String DRIVEN_ADAPTER_PERSISTENCE = "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.persistence";
    static private final String DRIVEN_ADAPTER_MESSAGING =   "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub";

    private JexxaMain jexxaMain;

    @BeforeEach
    void initTest()
    {
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
        
        jexxaMain = new JexxaMain(BookStoreServiceTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER_MESSAGING)
                .addToInfrastructure(DRIVEN_ADAPTER_PERSISTENCE);
    }

    @Test
    void receiveBook() throws InvalidISBNException
    {
        //Arrange
        var objectUnderTest = jexxaMain.getInstanceOfPort(BookStoreService.class);
        var isbn13 = "978-3-86490-387-8";
        var amount = 5;

        //Act
        objectUnderTest.receiveBook(isbn13, amount);

        //Assert
        assertEquals( amount, objectUnderTest.amountInStock(isbn13) );
    }


}