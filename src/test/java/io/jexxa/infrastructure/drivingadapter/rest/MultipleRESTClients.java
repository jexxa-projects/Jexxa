package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.TestTags.INTEGRATION_TEST;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.utils.ThrowingConsumer;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(INTEGRATION_TEST)
public class MultipleRESTClients
{
    final ApplicationService applicationService = new ApplicationService();
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_TYPE = "application/json";
    static final String METHOD_GET_SIMPLE_VALUE = "increment";
    static final int MAX_COUNTER = 1000;
    static final int MAX_THREADS = 5;

    final String restPath = "http://localhost:7000/ApplicationService/";


    static public class ApplicationService
    {
        int counter = 0;
        List<Integer> usedCounter = new ArrayList<>();

        public void increment()
        {
            if ( counter < MAX_COUNTER )
            {
                ++counter;
                usedCounter.add(counter);
            }
        }

        public int getCounter()
        {
            return counter;
        }

        public List<Integer> getUsedCounter()
        {
            return usedCounter;
        }
    }


    @Test
    public void synchronizeMultipleClients()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain("MultiThreading");
        jexxaMain
                .bind(RESTfulRPCAdapter.class).to(applicationService)
                .start();

        var exceptionList = new ArrayList<Throwable>();
        var clientPool = Stream.generate(() -> new Thread(this::incrementService))
                .limit(MAX_THREADS)
                .collect(Collectors.toList());

        List<Integer> expectedResult = IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(toList());


        //Act
        clientPool.stream()
                .forEach(Thread::start);

        clientPool.stream()
                .forEach(ThrowingConsumer.exceptionCollector(Thread::join, exceptionList));


        //Assert
        jexxaMain.stop();
        Unirest.shutDown();

        Assertions.assertEquals(expectedResult, applicationService.getUsedCounter());
        Assertions.assertTrue(exceptionList.isEmpty());

    }

    void incrementService()
    {
        while ( applicationService.getCounter() < MAX_COUNTER )
        {
            //Act
            Unirest.post(restPath + METHOD_GET_SIMPLE_VALUE)
                    .header(CONTENT_TYPE, APPLICATION_TYPE)
                    .asJson();
        }
    }

}
