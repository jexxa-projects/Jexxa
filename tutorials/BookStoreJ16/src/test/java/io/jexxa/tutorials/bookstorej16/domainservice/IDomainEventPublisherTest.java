package io.jexxa.tutorials.bookstorej16.domainservice;

import io.jexxa.core.JexxaMain;
import io.jexxa.jexxatest.JexxaTest;
import io.jexxa.tutorials.bookstorej16.BookStoreJ16Application;
import io.jexxa.tutorials.bookstorej16.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstorej16.domain.valueobject.ISBN13;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.tutorials.bookstorej16.domain.domainevent.BookSoldOut.bookSoldOut;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IDomainEventPublisherTest
{
    //Declare the packages that should be used by Jexxa
    private static final String DRIVEN_ADAPTER  = BookStoreJ16Application.class.getPackageName() + ".infrastructure.drivenadapter";
    private static final String OUTBOUND_PORTS  = BookStoreJ16Application.class.getPackageName() + ".domainservice";

    private JexxaTest jexxaTest;
    private IDomainEventPublisher objectUnderTest;

    @BeforeEach
    void initTest()
    {
        var jexxaMain = new JexxaMain(getClass().getSimpleName());
        jexxaMain
                .addToApplicationCore(OUTBOUND_PORTS)
                .addToInfrastructure(DRIVEN_ADAPTER);

        jexxaTest = new JexxaTest(jexxaMain);
        objectUnderTest = jexxaTest.getInstanceOfPort(IDomainEventPublisher.class);
    }

    @Test
    void testDomainEvent()
    {
        // Arrange
        var messageRecorder = jexxaTest.getMessageRecorder(IDomainEventPublisher.class);
        var isbn13 = new ISBN13("978-3-86490-387-8");

        // Act
        objectUnderTest.publish(bookSoldOut(isbn13));

        // Assert
        assertDoesNotThrow(() -> messageRecorder.getMessage(BookSoldOut.class));
    }

    @Test
    void testInvalidDomainEvent()
    {
        // Arrange
        var isbn13 = new ISBN13("978-3-86490-387-8");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.publish(isbn13));
    }

}