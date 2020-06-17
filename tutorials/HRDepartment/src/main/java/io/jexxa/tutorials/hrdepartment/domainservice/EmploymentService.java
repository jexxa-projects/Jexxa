package io.jexxa.tutorials.hrdepartment.domainservice;

import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStarted;
import io.jexxa.tutorials.hrdepartment.domain.domainevent.EmploymentStopped;

public interface EmploymentService
{
    void announceStoppedEmployment(EmploymentStopped employmentStopped);
    void announceStartedEmployment(EmploymentStarted employmentStarted);
}
