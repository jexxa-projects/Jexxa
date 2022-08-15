package io.jexxa.core;


import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.annotation.InvalidApplicationService;
import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.applicationservice.InvalidConstructorApplicationService;
import io.jexxa.application.applicationservice.JexxaApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.model.JexxaEntityRepository;
import io.jexxa.application.domainservice.BootstrapJexxaEntities;
import io.jexxa.application.infrastructure.drivingadapter.generic.ProxyDrivingAdapter;
import io.jexxa.application.infrastructure.drivingadapter.portadapter.ProxyPortAdapter;
import io.jexxa.application.infrastructure.drivingadapter.portadapter.ThrowingPortAdapter;
import io.jexxa.application.infrastructure.drivingadapter.portadapter.PortAdapter;
import io.jexxa.core.convention.PortConventionViolation;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.imdb.IMDBRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static io.jexxa.TestConstants.UNIT_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Execution(ExecutionMode.SAME_THREAD)
@Tag(UNIT_TEST)
class JexxaMainTest
{
    private JexxaMain objectUnderTest;

    @BeforeEach
    void initTests()
    {
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = new JexxaMain(JexxaTestApplication.class);
    }

    @AfterEach
    void tearDownTests()
    {
        if (objectUnderTest != null)
        {
            objectUnderTest.stop();
        }
    }



    @Test
    void bindToPort()
    {
        //Arrange - All done in initTests

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .bind(ProxyDrivingAdapter.class).to(SimpleApplicationService.class)
                .disableBanner()
                .start();

        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyDrivingAdapter.class)
                .getPortList()
                .stream()
                .filter( element -> SimpleApplicationService.class.equals(element.getClass()) )
                .findFirst();

        assertTrue(result.isPresent());
    }

    @Test
    void conditionalBindToPort()
    {
        //Arrange - All done in initTests

        //Act: Conditional bind (evaluating to false) a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .conditionalBind(() -> false, ProxyDrivingAdapter.class).to(SimpleApplicationService.class)
                .disableBanner()
                .start();

        //Assert that no binding has been performed
        var result = objectUnderTest.getDrivingAdapter(ProxyDrivingAdapter.class);

        assertTrue(result.getPortList().isEmpty());
    }


    @Test   // Support of dependency injection
    void bindToPortWithDrivenAdapter()
    {
        //Arrange - All done in initTests

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .bind(ProxyDrivingAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .disableBanner()
                .start();


        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyDrivingAdapter.class)
                .getPortList()
                .stream()
                .filter( element -> ApplicationServiceWithDrivenAdapters.class.equals(element.getClass()) )
                .findFirst();

        assertTrue(result.isPresent());
    }


    @Test
    void bindToAnnotatedPorts()
    {
        //Arrange - All done in initTests

        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest
                .bind(ProxyDrivingAdapter.class).toAnnotation(ValidApplicationService.class)
                .disableBanner()
                .start();

        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyDrivingAdapter.class)
                .getPortList()
                .stream()
                .filter( element -> SimpleApplicationService.class.equals(element.getClass()) )
                .findFirst();

        assertTrue(result.isPresent());
    }

    @Test
    void bindToAnnotatedInvalidPorts()
    {
        //Arrange --
        var drivingAdapter = objectUnderTest.bind(ProxyDrivingAdapter.class);

        //Act / Assert
        assertThrows(RuntimeException.class, () -> drivingAdapter.toAnnotation(InvalidApplicationService.class));
    }

    @Test
    void invalidBindToPortAdapter()
    {
        //Arrange - All done in initTests
        objectUnderTest = new JexxaMain(JexxaMainTest.class);

        var drivingAdapter = objectUnderTest.bind(ProxyDrivingAdapter.class);

        //Act /Assert
        assertThrows(PortConventionViolation.class, () -> drivingAdapter.to(PortAdapter.class));
    }

    @Test
    void bindToMultiplePortAdapterOfSameType()
    {
        //Arrange
        var expectedDrivingAdapterInstanceCount = 1; // Since DrivingAdapter are treated as singletons we expect 1 instance
        var expectedProxyAdapterInstanceCount = 1;   // Since PortAdapter are treated as singletons we expect 1 instance
        ProxyDrivingAdapter.resetInstanceCount();
        ProxyPortAdapter.resetInstanceCount();

        //Act
        objectUnderTest
                .bind(ProxyDrivingAdapter.class).to(ProxyPortAdapter.class)
                .bind(ProxyDrivingAdapter.class).to(ProxyPortAdapter.class);

        //Assert
        assertEquals(expectedDrivingAdapterInstanceCount, ProxyDrivingAdapter.getInstanceCount());
        assertEquals(expectedProxyAdapterInstanceCount, ProxyPortAdapter.getInstanceCount());
    }

    @Test
    void bindToThrowingAdapter()
    {
        //Arrange
        ProxyDrivingAdapter.resetInstanceCount();

        //Act
        var result = objectUnderTest.bind(ProxyDrivingAdapter.class);

        //Assert
        assertThrows( RuntimeException.class, () -> result.to(ThrowingPortAdapter.class));

    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void bootstrapService()
    {
        //Arrange
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);

        //Act
        objectUnderTest.bootstrap(BootstrapJexxaEntities.class).with(BootstrapJexxaEntities::initDomainData);

        var jexxaApplicationService = objectUnderTest.getInstanceOfPort(JexxaApplicationService.class);

        //Assert
        assertTrue(jexxaApplicationService.getAggregateCount() > 0);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void getInstanceOfInvalidPort()
    {
        //Arrange --

        //Act/Assert
        assertThrows(PortConventionViolation.class, () -> objectUnderTest.getInstanceOfPort(InvalidConstructorApplicationService.class));
    }

    @Test
    void getInstanceOfOutboundPort()
    {
        //Arrange --

        //Act/Assert
        assertNotNull( objectUnderTest.getInstanceOfPort(JexxaEntityRepository.class));
    }

    @Test
    void testJexxaException()
    {
        //Arrange
        var jexxaException = new JexxaMain.JexxaExceptionHandler(objectUnderTest);

        //Act
        jexxaException.uncaughtException(Thread.currentThread(), new IllegalStateException("Test Exception Handler", new Throwable("Test Exception Handler as part of Unit tests")));

        //Assert
        assertFalse(objectUnderTest.getBoundedContext().isRunning());
    }

    @Test
    void testAddDDDPackages() {
        //Act
        var result = objectUnderTest.addDDDPackages(JexxaMainTest.class);

        //Assert
        assertEquals(objectUnderTest, result);

        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".applicationservice"));
        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".domainservice"));
        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".domainprocessservice"));

        assertTrue(objectUnderTest.getInfrastructure().contains( JexxaMainTest.class.getPackageName() + ".infrastructure.drivenadapter"));
        assertTrue(objectUnderTest.getInfrastructure().contains( JexxaMainTest.class.getPackageName() + ".infrastructure.drivingadapter"));
    }

}
