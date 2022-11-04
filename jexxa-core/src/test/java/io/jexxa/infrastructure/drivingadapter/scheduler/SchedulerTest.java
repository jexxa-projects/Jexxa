package io.jexxa.infrastructure.drivingadapter.scheduler;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.core.JexxaMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchedulerTest {
    private JexxaMain jexxaMain;

    @BeforeEach
    void initBeforeEach()
    {
        jexxaMain = new JexxaMain(SchedulerTest.class);
    }

    @Test
    void testFixedRateScheduler()
    {
        //Arrange
        jexxaMain.bind(Scheduler.class).to(FixedRateScheduler.class);

        //Act
        jexxaMain.start();

        //Assert
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollDelay(50, MILLISECONDS)
                .until(() -> jexxaMain.getInstanceOfPort(SimpleApplicationService.class).getSimpleValue() > 100);

        jexxaMain.stop();
    }

    @Test
    void testFixedDelayScheduler()
    {
        //Arrange
        jexxaMain.bind(Scheduler.class).to(FixedDelayScheduler.class);

        //Act
        jexxaMain.start();

        //Assert
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollDelay(50, MILLISECONDS)
                .until(() -> jexxaMain.getInstanceOfPort(SimpleApplicationService.class).getSimpleValue() > 100);

        jexxaMain.stop();
    }

    @Test
    void testMissingScheduledAnnotation()
    {
        //Arrange
        var drivingAdapter = jexxaMain.bind(Scheduler.class);

        //Act / Assert
        assertThrows( IllegalArgumentException.class,  () -> drivingAdapter.to(MissingScheduledAnnotation.class));
    }

    @Test
    void testInvalidScheduledAnnotation()
    {
        //Arrange
        var drivingAdapter = jexxaMain.bind(Scheduler.class);

        //Act / Assert
        assertThrows( IllegalArgumentException.class,  () -> drivingAdapter.to(InvalidScheduledAnnotation.class));
    }

    public static class FixedRateScheduler {
        private final SimpleApplicationService simpleApplicationService;
        public FixedRateScheduler(SimpleApplicationService simpleApplicationService)
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

    public static class FixedDelayScheduler {
        private final SimpleApplicationService simpleApplicationService;
        public FixedDelayScheduler(SimpleApplicationService simpleApplicationService)
        {
            this.simpleApplicationService = simpleApplicationService;
        }

        @Scheduled(fixedDelay = 10, timeUnit = MILLISECONDS)
        @SuppressWarnings("unused")
        public void run()
        {
            simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue()+1);
        }
    }


    public static class MissingScheduledAnnotation {
        private final SimpleApplicationService simpleApplicationService;
        public MissingScheduledAnnotation(SimpleApplicationService simpleApplicationService)
        {
            this.simpleApplicationService = simpleApplicationService;
        }

        @SuppressWarnings("unused")
        public void run()
        {
            simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue()+1);
        }
    }

    public static class InvalidScheduledAnnotation {
        private final SimpleApplicationService simpleApplicationService;
        public InvalidScheduledAnnotation(SimpleApplicationService simpleApplicationService)
        {
            this.simpleApplicationService = simpleApplicationService;
        }

        @SuppressWarnings("unused")
        @Scheduled(timeUnit = MILLISECONDS)
        public void run()
        {
            simpleApplicationService.setSimpleValue(simpleApplicationService.getSimpleValue()+1);
        }
    }

}
