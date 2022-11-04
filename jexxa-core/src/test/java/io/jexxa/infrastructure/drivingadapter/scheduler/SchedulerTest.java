package io.jexxa.infrastructure.drivingadapter.scheduler;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.core.JexxaMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

class SchedulerTest {
    private JexxaMain jexxaMain;

    @BeforeEach
    void initBeforeEach()
    {
        jexxaMain = new JexxaMain(SchedulerTest.class);
    }

    @Test
    void testTimerInterval()
    {
        //Arrange
        jexxaMain.bind(Scheduler.class).to(ApplicationServiceTimer.class);

        //Act
        jexxaMain.start();

        //Assert
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollDelay(50, MILLISECONDS)
                .until(() -> jexxaMain.getInstanceOfPort(SimpleApplicationService.class).getSimpleValue() > 100);

        jexxaMain.stop();
    }


    public static class ApplicationServiceTimer {
        private final SimpleApplicationService simpleApplicationService;
        public ApplicationServiceTimer(SimpleApplicationService simpleApplicationService)
        {
            this.simpleApplicationService = simpleApplicationService;
        }

        @Scheduled(fixedRate = 10, timeUnit = MILLISECONDS)
        @SuppressWarnings("unused")
        public void run()
        {
            simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue()+1);
        }
    }
}
