package io.jexxa.infrastructure.threading;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiThreading
{
    final ApplicationService applicationService = new ApplicationService();
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_TYPE = "application/json";
    static final String METHOD_GET_SIMPLE_VALUE = "increment";

    final String restPath = "http://localhost:7000/ApplicationService/";


    static public class ApplicationService
    {
        int counter = 0;
        List<Integer> usedCounter = new ArrayList<>();

        public void increment()
        {
            ++counter;
            usedCounter.add(counter);
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
    public void multiDrivingAdapter()
    {
        JexxaMain jexxaMain = new JexxaMain("MultiThreading");

        jexxaMain
                .bind(RESTfulRPCAdapter.class).to(applicationService)
                .start();

        Thread t1 = new Thread(this::incrementService);
        Thread t2 = new Thread(this::incrementService);

        t1.start();
        t2.start();

        try
        {
            t1.join();
            t2.join();
        } catch (InterruptedException e)
        {
            //;
        }

        jexxaMain.stop();
        Unirest.shutDown();
        //Assert
        List<Integer> expectedResult = IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(toList());
        Assertions.assertEquals(expectedResult, applicationService.getUsedCounter());
        
    }

    void incrementService()
    {

        while ( applicationService.getCounter() < 1000 )
        {
            //Act
            var response = Unirest.post(restPath + METHOD_GET_SIMPLE_VALUE)
                    .header(CONTENT_TYPE, APPLICATION_TYPE)
                    .asJson();


/*            if (!response.isSuccess() );
            {
                System.out.println("ERROR" + response.getBody() + response.toString());
                return;
            }
  */

            /*synchronized (applicationService) {
                if ( applicationService.getCounter() < 1000 )
                {
                    applicationService.increment();
                }
            }*/
        }
    }

}
