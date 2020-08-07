package io.jexxa.tutorials.bookstore.domainservice;


import static org.junit.jupiter.api.Assertions.assertFalse;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.tutorials.bookstore.applicationservice.BookStoreService;
import io.jexxa.tutorials.bookstore.domain.aggregate.Book;
import io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub.DomainEventStubPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferenceLibraryTest
{
    private static final String DRIVEN_ADAPTER_PERSISTENCE = "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.persistence";
    private static final String DRIVEN_ADAPTER_MESSAGING =   "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.stub";
    
    private JexxaMain jexxaMain;

    @BeforeEach
    void initTest()
    {
        // Here you can define the desired DB strategy without adjusting your tests
        // Within your tests you can completely focus on the domain logic which allows
        // you to run the tests as unit tests within daily development or as integration
        // tests on a build server
        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);

        jexxaMain = new JexxaMain(ReferenceLibraryTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER_PERSISTENCE)
                .addToInfrastructure(DRIVEN_ADAPTER_MESSAGING);

        DomainEventStubPublisher.clear();

        //Clean up the repository 
        RepositoryManager.getInstance()
                .getStrategy(Book.class, Book::getISBN13, jexxaMain.getProperties())
                .removeAll();
    }


    @Test
    void validateAddLatestBooks()
    {
        //Arrange : Get the inbound port that we would like to test
        var objectUnderTest = jexxaMain.getInstanceOfPort(ReferenceLibrary.class);
        var bookStore = jexxaMain.getInstanceOfPort(BookStoreService.class);

        //Act
        objectUnderTest.addLatestBooks();

        //Assert: After adding books via our service, our bookstore must know theses books
        assertFalse( bookStore.getBooks().isEmpty() );
    }
}