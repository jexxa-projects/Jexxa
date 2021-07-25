package io.jexxa.tutorials.contractmanagement.domainservice;

import java.util.List;
import java.util.Optional;

import io.jexxa.addend.applicationcore.Repository;
import io.jexxa.tutorials.contractmanagement.domain.aggregate.Contract;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;

@Repository
public interface IContractRepository
{
    void add(Contract contract);

    void update(Contract contract);

    void remove(ContractNumber contractNumber);

    List<Contract> getByAdvisor(String advisor);

    Contract get(ContractNumber contractNumber);

    List<Contract> getAll();

    List<Contract> getSignedContracts();

    List<Contract> getUnsignedContracts();

    Optional<Contract> getHighestContractNumber();
}
