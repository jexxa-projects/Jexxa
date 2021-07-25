package io.jexxa.tutorials.domaineventstore.domainservice;

import java.util.List;
import java.util.Optional;

import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.tutorials.domaineventstore.domain.aggregate.Contract;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;

@Repository
public interface IContractRepository
{
    void add(Contract contract);

    void remove(ContractNumber contractNumber);

    List<Contract> getTerminated();

    List<Contract> getByAdvisor(String advisor);

    Contract get(ContractNumber contractNumber);

    List<Contract> getAll();

    Optional<Contract> getHighestContractNumber();
}
