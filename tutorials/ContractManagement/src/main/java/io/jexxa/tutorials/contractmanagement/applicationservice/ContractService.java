package io.jexxa.tutorials.contractmanagement.applicationservice;

import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.tutorials.contractmanagement.domain.aggregate.Contract;
import io.jexxa.tutorials.contractmanagement.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.contractmanagement.domainservice.IContractRepository;
import io.jexxa.tutorials.contractmanagement.domainservice.IDomainEventStore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static io.jexxa.tutorials.contractmanagement.domain.aggregate.Contract.newContract;

@SuppressWarnings("unused")
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

    public void signContract( ContractNumber contractNumber )
    {
        var contract = contractRepository.get(contractNumber);
        var domainEvent= contract.sign();

        contractRepository.update(contract);
        domainEventStore.add(domainEvent);
    }

    public List<ContractNumber> getUnsignedContracts()
    {
        return contractRepository
                .getUnsignedContracts()
                .stream()
                .map(Contract::getContractNumber)
                .collect(Collectors.toList());
    }

    public List<ContractSigned> getAllSignedContracts()
    {
        return domainEventStore.get();
    }

    public List<ContractSigned> getSignedContracts(int month, int year)
    {
        var startDate = LocalDate.of(year, month, 1);
        var endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        return domainEventStore.get(
                startDate.atStartOfDay().toInstant(ZoneOffset.UTC),
                endDate.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC));
    }

    public List<ContractNumber> getContractsByAdvisor(String advisor)
    {
        return contractRepository
                .getByAdvisor(advisor)
                .stream()
                .map(Contract::getContractNumber)
                .collect(Collectors.toList());
    }

    private ContractNumber getNextContractNumber()
    {
        return contractRepository
                .getHighestContractNumber()
                .map(contract -> new ContractNumber(contract.getContractNumber().getValue() + 1))
                .orElseGet(() -> new ContractNumber(1));
    }
}
