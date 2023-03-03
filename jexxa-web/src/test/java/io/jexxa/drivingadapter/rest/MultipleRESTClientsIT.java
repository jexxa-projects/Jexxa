package io.jexxa.drivingadapter.rest;

import io.jexxa.TestConstants;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.common.function.ThrowingConsumer;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestConstants.INTEGRATION_TEST)
class MultipleRESTClientsIT
{
    private static final String METHOD_GET_SIMPLE_VALUE = "increment";
    private static final int MAX_COUNTER = 1000;
    private static final int MAX_THREADS = 5;

    private IncrementApplicationService applicationService;
    private JexxaMain jexxaMain;


    @BeforeEach
    void setUp()
    {
        jexxaMain = new JexxaMain(JexxaTestApplication.class);
        jexxaMain.disableBanner()
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
                .toList();

        var exceptionList = new ArrayList<Throwable>();

        //Act
        clientPool.forEach(Thread::start);

        clientPool.forEach(ThrowingConsumer.exceptionCollector(Thread::join, exceptionList));


        //Assert
        assertEquals(expectedResult, applicationService.getUsedCounter());
        assertTrue(exceptionList.isEmpty());
    }

    void incrementService()
    {
        while ( applicationService.getCounter() < MAX_COUNTER )
        {
            //Act
            var restPath = "http://localhost:7500/IncrementApplicationService/";
            var response = Unirest.post(restPath + METHOD_GET_SIMPLE_VALUE)
                    .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                    .asJson();
            if (!response.isSuccess())
            {
                throw new IllegalArgumentException("HTTP Response Error: " + response.getStatus() + " " + response.getStatusText() );
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
