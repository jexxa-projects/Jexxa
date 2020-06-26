package io.jexxa.tutorials.hrdepartment.applicationservice;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.tutorials.hrdepartment.infrastructure.drivenadapter.messaging.JMSEmploymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeServiceTest
{
    JexxaMain jexxaMain;

    EmployeeService objectUnderTest;

    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(getClass().getSimpleName());

        jexxaMain.addToApplicationCore("io.jexxa.tutorials.hrdepartment.domainservice")
                .addToInfrastructure("io.jexxa.tutorials.hrdepartment.infrastructure");

        RepositoryManager.getInstance().setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = jexxaMain.getInstanceOfPort(EmployeeService.class);
    }

    @Test
    void createEmployee()
    {
        //Arrange - nothing

        //Act
        var result = objectUnderTest.createEmployee();

        //Assert
        assertNotNull(result);
        assertTrue(objectUnderTest.getAllEmployees().contains(result));
        assertEquals(result.getValue(), objectUnderTest.getAllEmployees().size());
    }

    @Test
    void startEmployment()
    {
        //Arrange 
        jexxaMain.bind(JMSAdapter.class).to(EmployeeListener.class)
                .start();

        var messageListener = jexxaMain.getInstanceOfPort(EmployeeListener.class);

        //Act
        var result = objectUnderTest.createEmployee();

        objectUnderTest.startEmployment(result, LocalDate.now());

        await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> !messageListener.getMessageList().isEmpty());


        //Assert
        assertNotNull(result);
        assertEquals(1, objectUnderTest.getAllEmployees().size());
        assertEquals(1, messageListener.getMessageList().size());

        jexxaMain.stop();
    }

    public static class EmployeeListener implements MessageListener
    {
        private final List<TextMessage> messageList = new ArrayList<>();

        @Override
        @JMSConfiguration(destination = JMSEmploymentService.EMPLOYMENT_SERVICE_TOPIC, messagingType = JMSConfiguration.MessagingType.TOPIC )
        public void onMessage(Message message)
        {
            if (message != null)
            {
                messageList.add((TextMessage)message);
            }
        }

        List<TextMessage> getMessageList()
        {
            return messageList;
        }
    }

}