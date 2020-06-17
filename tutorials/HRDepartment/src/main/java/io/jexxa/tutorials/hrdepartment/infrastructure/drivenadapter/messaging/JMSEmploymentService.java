package io.jexxa.tutorials.hrdepartment.infrastructure.drivenadapter.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStarted;
import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStopped;
import io.jexxa.tutorials.hrdepartment.domainservice.EmploymentService;

public class JMSEmploymentService implements EmploymentService
{
    private final JMSSender jmsSender;
    private static final String EMPLOYMENT_SERVICE_TOPIC = "EmploymentServiceTopic";

    private JMSEmploymentService(JMSSender jmsSender)
    {
        this.jmsSender = jmsSender;
    }

    @Override
    public void announceStoppedEmployment(EmploymentStopped employmentStopped)
    {
        jmsSender.sendToTopic(employmentStopped, EMPLOYMENT_SERVICE_TOPIC);
    }

    @Override
    public void announceStartedEmployment(EmploymentStarted employmentStarted)
    {
        jmsSender.sendToTopic(employmentStarted, EMPLOYMENT_SERVICE_TOPIC);
    }

    public static EmploymentService getInstance(Properties properties )
    {
        return new JMSEmploymentService(new JMSSender(properties));
    }
}
