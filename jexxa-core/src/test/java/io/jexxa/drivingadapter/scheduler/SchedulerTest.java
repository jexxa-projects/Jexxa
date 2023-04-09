package io.jexxa.drivingadapter.scheduler;

import io.jexxa.core.JexxaMain;
import io.jexxa.drivingadapter.scheduler.portadapter.FixedDelayIncrementer;
import io.jexxa.drivingadapter.scheduler.portadapter.FixedRateIncrementer;
import io.jexxa.drivingadapter.scheduler.portadapter.InvalidScheduledAnnotation;
import io.jexxa.drivingadapter.scheduler.portadapter.MissingScheduledAnnotation;
import io.jexxa.drivingadapter.scheduler.portadapter.MultipleIncrementer;
import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
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
        jexxaMain.addToInfrastructure(FixedRateIncrementer.class.getPackageName());
        jexxaMain.disableBanner();
    }

    @Test
    void testFixedRateScheduler()
    {
        //Arrange
        jexxaMain.bind(Scheduler.class).to(FixedRateIncrementer.class);

        //Act
        jexxaMain.start();

        //Assert that simple value is incremented > 100 within 5 seconds
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
        jexxaMain.bind(Scheduler.class).to(FixedDelayIncrementer.class);

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
    void testMultipleScheduler()
    {
        //Arrange
        jexxaMain.bind(Scheduler.class).to(MultipleIncrementer.class);

        //Act
        jexxaMain.start();

        //Assert that both values are incremented
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollDelay(50, MILLISECONDS)
                .until(() -> jexxaMain.getInstanceOfPort(SimpleApplicationService.class).getSimpleValue() > 100
                && jexxaMain.getInstanceOfPort(SimpleApplicationService.class).getSimpleValueObject().getValue() > 100);

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
