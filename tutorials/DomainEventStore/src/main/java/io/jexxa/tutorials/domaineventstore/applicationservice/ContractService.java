package io.jexxa.tutorials.domaineventstore.applicationservice;

import static io.jexxa.tutorials.domaineventstore.domain.aggregate.Contract.newContract;

import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.domaineventstore.domainservice.IContractRepository;
import io.jexxa.tutorials.domaineventstore.domainservice.IDomainEventStore;

@ApplicationService
public class ContractService
{
    private final IContractRepository contractRepository;
    private final IDomainEventStore domainEventStore;

    public ContractService(IContractRepository contractRepository, IDomainEventStore domainEventStore)
    {
        this.contractRepository = contractRepository;
        this.domainEventStore = domainEventStore;
    }

    public ContractNumber createNewContract(String advisor)
    {
        var newContract = newContract(getNextContractNumber(), advisor);
        contractRepository.add(newContract);
        return newContract.getContractNumber();
    }

    private ContractNumber getNextContractNumber()
    {
        return contractRepository
                .getHighestContractNumber()
                .map(contract -> new ContractNumber(contract.getContractNumber().getValue() + 1))
                .orElseGet(() -> new ContractNumber(1));
    }
}
