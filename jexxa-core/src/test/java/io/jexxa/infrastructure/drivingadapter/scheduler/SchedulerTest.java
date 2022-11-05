package io.jexxa.infrastructure.drivingadapter.scheduler;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.scheduler.portadapter.FixedDelayScheduler;
import io.jexxa.infrastructure.drivingadapter.scheduler.portadapter.FixedRateScheduler;
import io.jexxa.infrastructure.drivingadapter.scheduler.portadapter.InvalidScheduledAnnotation;
import io.jexxa.infrastructure.drivingadapter.scheduler.portadapter.MissingScheduledAnnotation;
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
        jexxaMain.disableBanner();
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


}
