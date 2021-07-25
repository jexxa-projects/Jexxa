package io.jexxa.tutorials.contractmanagement.domainservice;

import java.time.Instant;
import java.util.List;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.tutorials.contractmanagement.domain.domainevent.ContractSigned;

@InfrastructureService
public interface IDomainEventStore
{
    void add(ContractSigned domainEvent);

    List<ContractSigned> get(Instant startTime, Instant endTime);

    List<ContractSigned> get();
}
