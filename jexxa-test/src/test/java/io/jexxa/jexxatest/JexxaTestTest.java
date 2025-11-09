package io.jexxa.jexxatest;

import io.jexxa.core.factory.InvalidAdapterException;
import io.jexxa.jexxatest.application.DomainEventPublisher;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.applicationservice.ApplicationServiceWithInvalidDrivenAdapters;
import io.jexxa.testapplication.domain.model.JexxaAggregateRepository;
import io.jexxa.testapplication.domain.model.JexxaDomainEvent;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domainservice.BootstrapJexxaAggregates;
import io.jexxa.testapplication.domainservice.InvalidConstructorParameterService;
import io.jexxa.testapplication.domainservice.NotImplementedService;
import io.jexxa.testapplication.infrastructure.drivenadapter.persistence.JexxaAggregateRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jexxatest.JexxaTest.getJexxaTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JexxaTestTest
{
    private JexxaTest jexxaTest;

    @BeforeEach
    void setUp()
    {
        //Arrange
        jexxaTest = getJexxaTest(JexxaTestApplication.class);
    }

    @Test
    void requestInvalidPort()
    {
        assertThrows(InvalidAdapterException.class, () -> jexxaTest.getInstanceOfPort(ApplicationServiceWithInvalidDrivenAdapters.class));
    }

    @Test
    void validateRepository()
    {
        //Arrange
        var jexxaRepository = jexxaTest.getRepository(JexxaAggregateRepository.class);

        //Act
        jexxaTest.getInstanceOfPort(BootstrapJexxaAggregates.class).initDomainData();

        //Assert
        assertFalse(jexxaRepository.get().isEmpty());
    }

    @Test
    void validateRepositoryIsInterface()
    {
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jexxaTest.getRepository(JexxaAggregateRepositoryImpl.class));
    }


    @Test
    void recordDomainEvent()
    {
        //Arrange
        var domainEvent = new JexxaDomainEvent(new JexxaValueObject(1));
        var objectUnderTest = jexxaTest.getDomainEventRecorder(JexxaDomainEvent.class, DomainEventPublisher::subscribe);

        //Act
        DomainEventPublisher.publish(domainEvent);


        //Assert MessageRecorder
        assertFalse(objectUnderTest.get().isEmpty());
        assertEquals(1, objectUnderTest.get().size());
        assertEquals(domainEvent, objectUnderTest.get().getFirst());
    }


    @Test
    void recordAllDomainEvent()
    {
        //Arrange
        var domainEvent = new JexxaDomainEvent(new JexxaValueObject(1));
        var objectUnderTest = jexxaTest.getDomainEventRecorder(DomainEventPublisher::subscribe);

        //Act
        DomainEventPublisher.publish(domainEvent);


        //Assert MessageRecorder
        assertFalse(objectUnderTest.get().isEmpty());
        assertEquals(1, objectUnderTest.get().size());
        assertEquals(domainEvent, objectUnderTest.get().getFirst());
    }



    @Test
    void repositoryNotAvailable()
    {
        //Act/Assert
        assertThrows( IllegalArgumentException.class, () -> jexxaTest.getRepository(NotImplementedService.class) );

        //Act/Assert
        assertThrows( InvalidAdapterException.class, () -> jexxaTest.getRepository(InvalidConstructorParameterService.class) );
    }

    @Test
    void jexxaTestProperties()
    {
        //Act/Assert
        assertTrue( jexxaTest.getProperties().containsKey("io.jexxa.test.project") );
    }


}
