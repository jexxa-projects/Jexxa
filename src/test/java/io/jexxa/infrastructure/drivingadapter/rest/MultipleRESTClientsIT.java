package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.TestTags.INTEGRATION_TEST;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.utils.ThrowingConsumer;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(INTEGRATION_TEST)
class MultipleRESTClientsIT
{
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_TYPE = "application/json";
    static final String METHOD_GET_SIMPLE_VALUE = "increment";
    static final int MAX_COUNTER = 1000;
    static final int MAX_THREADS = 5;

    final String restPath = "http://localhost:7000/IncrementApplicationService/";

    IncrementApplicationService applicationService;
    JexxaMain jexxaMain;


    @BeforeEach
    void setUp()
    {
        jexxaMain = new JexxaMain(MultipleRESTClientsIT.class.getSimpleName());
        jexxaMain
                .bind(RESTfulRPCAdapter.class).to(IncrementApplicationService.class)
                .start();

        applicationService = jexxaMain.getInstanceOfPort(IncrementApplicationService.class);
    }

    @Test
    void synchronizeMultipleClients()
    {
        //Arrange
        applicationService.setMaxCounter(MAX_COUNTER);
        List<Integer> expectedResult = IntStream.rangeClosed(1, MAX_COUNTER)
                .boxed()
                .collect(toList());

        var clientPool = Stream.generate(() -> new Thread(this::incrementService))
                .limit(MAX_THREADS)
                .collect(Collectors.toList());

        var exceptionList = new ArrayList<Throwable>();
        
        //Act
        clientPool.forEach(Thread::start);

        clientPool.forEach(ThrowingConsumer.exceptionCollector(Thread::join, exceptionList));


        //Assert
        Assertions.assertEquals(expectedResult, applicationService.getUsedCounter());
        Assertions.assertTrue(exceptionList.isEmpty());
    }

    void incrementService()
    {
        while ( applicationService.getCounter() < MAX_COUNTER )
        {
            //Act
            var response = Unirest.post(restPath + METHOD_GET_SIMPLE_VALUE)
                    .header(CONTENT_TYPE, APPLICATION_TYPE)
                    .asJson();
            if (!response.isSuccess())
            {
                throw new RuntimeException("HTTP Response Error: " + response.getStatus() + " " + response.getStatusText() );
            }
        }
    }

    @AfterEach
    void tearDown()
    {
        jexxaMain.stop();
        Unirest.shutDown();
    }

}
