package io.jexxa.tutorials.bookstore.domainservice;


import static org.junit.jupiter.api.Assertions.assertFalse;

import io.jexxa.core.JexxaMain;
import io.jexxa.test.JexxaTest;
import io.jexxa.tutorials.bookstore.applicationservice.BookStoreService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReferenceLibraryTest
{
    private static final String DRIVEN_ADAPTER = "io.jexxa.tutorials.bookstore.infrastructure.drivenadapter";
    private static final String DOMAIN_SERVICE = "io.jexxa.tutorials.bookstore.domainservice";

    private static JexxaMain jexxaMain;

    @BeforeAll
    static void initBeforeAll()
    {
        jexxaMain = new JexxaMain(ReferenceLibraryTest.class.getSimpleName());
        jexxaMain.addToInfrastructure(DRIVEN_ADAPTER)
                .addToApplicationCore(DOMAIN_SERVICE);
    }


    @Test
    void validateAddLatestBooks()
    {
        //Arrange : Get the inbound port that we would like to test
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);
        var objectUnderTest = jexxaTest.getInstanceOfPort(ReferenceLibrary.class);
        var bookStore = jexxaTest.getInstanceOfPort(BookStoreService.class);

        //Act
        objectUnderTest.addLatestBooks();

        //Assert: After adding books via our service, our bookstore must know theses books
        assertFalse( bookStore.getBooks().isEmpty() );
    }
}