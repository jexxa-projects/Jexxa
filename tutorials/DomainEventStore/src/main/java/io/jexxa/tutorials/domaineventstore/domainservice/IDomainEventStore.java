package io.jexxa.tutorials.domaineventstore.domainservice;

import java.time.Instant;
import java.util.List;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.tutorials.domaineventstore.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;

@InfrastructureService
public interface IDomainEventStore
{
    void add(ContractSigned domainEvent);

    List<ContractSigned> get(Instant startTime, Instant endTime);

    List<ContractSigned> getBatchNumbersLessThan(ContractNumber contractNumber);

    List<ContractSigned> getLatestBatches(int number);
}
