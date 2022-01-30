package io.jexxa.core;


import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.annotation.InvalidApplicationService;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.applicationservice.InvalidConstructorApplicationService;
import io.jexxa.application.applicationservice.JexxaApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domainservice.IJexxaEntityRepository;
import io.jexxa.application.domainservice.InitializeJexxaEntities;
import io.jexxa.application.infrastructure.drivingadapter.ProxyAdapter;
import io.jexxa.application.infrastructure.drivingadapter.ProxyPortAdapter;
import io.jexxa.application.infrastructure.drivingadapter.ThrowingPortAdapter;
import io.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceAdapter;
import io.jexxa.core.convention.PortConventionViolation;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.imdb.IMDBRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static io.jexxa.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;


@Execution(ExecutionMode.SAME_THREAD)
@Tag(UNIT_TEST)
class JexxaMainTest
{
    private JexxaMain objectUnderTest;
    private static final String CONTEXT_NAME = "HelloJexxa";

    @BeforeEach
    void initTests()
    {
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = new JexxaMain(CONTEXT_NAME);
        objectUnderTest
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToInfrastructure(JEXXA_DRIVING_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE);
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
                .bind(ProxyAdapter.class).to(SimpleApplicationService.class)

                .start();

        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyAdapter.class)
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
                .conditionalBind(() -> false, ProxyAdapter.class).to(SimpleApplicationService.class)
                .start();

        //Assert that no binding has been performed
        var result = objectUnderTest.getDrivingAdapter(ProxyAdapter.class);

        assertTrue(result.getPortList().isEmpty());
    }


    @Test   // Support of dependency injection
    void bindToPortWithDrivenAdapter()
    {
        //Arrange - All done in initTests

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(ProxyAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .start();


        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyAdapter.class)
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
                .bind(ProxyAdapter.class).toAnnotation(ApplicationService.class)
                .start();

        //Assert
        var result = objectUnderTest.getDrivingAdapter(ProxyAdapter.class)
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
        var drivingAdapter = objectUnderTest.bind(ProxyAdapter.class);

        //Act / Assert
        assertThrows(RuntimeException.class, () -> drivingAdapter.toAnnotation(InvalidApplicationService.class));
    }

    @Test
    void invalidBindToPortAdapter()
    {
        //Arrange - All done in initTests
        objectUnderTest = new JexxaMain(CONTEXT_NAME);
        objectUnderTest
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE);

        var drivingAdapter = objectUnderTest.bind(ProxyAdapter.class);

        //Act /Assert
        assertThrows(PortConventionViolation.class, () -> drivingAdapter.to(SimpleApplicationServiceAdapter.class));
    }

    @Test
    void bindToMultiplePortAdapterOfSameType()
    {
        //Arrange
        var expectedDrivingAdapterInstanceCount = 1; // Since DrivingAdapter are treated as singletons we expect 1 instance
        var expectedProxyAdapterInstanceCount = 1;   // Since PortAdapter are treated as singletons we expect 1 instance
        ProxyAdapter.resetInstanceCount();
        ProxyPortAdapter.resetInstanceCount();

        //Act
        objectUnderTest
                .bind(ProxyAdapter.class).to(ProxyPortAdapter.class)
                .bind(ProxyAdapter.class).to(ProxyPortAdapter.class);

        //Assert
        assertEquals(expectedDrivingAdapterInstanceCount, ProxyAdapter.getInstanceCount());
        assertEquals(expectedProxyAdapterInstanceCount, ProxyPortAdapter.getInstanceCount());
    }

    @Test
    void bindToThrowingAdapter()
    {
        //Arrange
        ProxyAdapter.resetInstanceCount();

        //Act
        var result = objectUnderTest.bind(ProxyAdapter.class);

        //Assert
        assertThrows( RuntimeException.class, () -> result.to(ThrowingPortAdapter.class));

    }


    @Test
    void bootstrapService()
    {
        //Arrange
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = new JexxaMain(CONTEXT_NAME);
        objectUnderTest.addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE)

        //Act
                .bootstrap(InitializeJexxaEntities.class).with(InitializeJexxaEntities::initDomainData);

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
        assertNotNull( objectUnderTest.getInstanceOfPort(IJexxaEntityRepository.class));
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
        objectUnderTest.addDDDPackages(JexxaMainTest.class);

        //Assert
        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".applicationservice"));
        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".domainservice"));
        assertTrue(objectUnderTest.getApplicationCore().contains( JexxaMainTest.class.getPackageName() + ".domainprocessservice"));

        assertTrue(objectUnderTest.getInfrastructure().contains( JexxaMainTest.class.getPackageName() + ".infrastructure.drivenadapter"));
        assertTrue(objectUnderTest.getInfrastructure().contains( JexxaMainTest.class.getPackageName() + ".infrastructure.drivingadapter"));
    }

}
