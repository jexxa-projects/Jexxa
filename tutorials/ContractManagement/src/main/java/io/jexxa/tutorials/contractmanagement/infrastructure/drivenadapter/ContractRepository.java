package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadata.ADVISOR;
import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadata.CONTRACT_NUMBER;
import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadata.CONTRACT_SIGNED;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.tutorials.contractmanagement.domain.aggregate.Contract;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.contractmanagement.domainservice.IContractRepository;

@SuppressWarnings("unused")
public class ContractRepository  implements IContractRepository
{
    enum ContractMetadata implements MetadataComparator
    {
        CONTRACT_NUMBER(Comparators.numberComparator(element -> element.getContractNumber().getValue())),

        CONTRACT_SIGNED(Comparators.booleanComparator(Contract::isSigned)),

        ADVISOR(Comparators.stringComparator(Contract::getAdvisor));

        private final Comparator<Contract, ?, ? > comparator;

        ContractMetadata(Comparator<Contract,?, ?> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<Contract, ?, ?> getComparator()
        {
            return comparator;
        }
    }


    private final IObjectStore<Contract, ContractNumber, ContractMetadata> objectStore;

    public ContractRepository(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(Contract.class, Contract::getContractNumber, ContractMetadata.class, properties);
    }

    @Override
    public void add(Contract contract)
    {
        objectStore.add(contract);
    }

    @Override
    public void update(Contract contract)
    {
        objectStore.update(contract);
    }

    @Override
    public void remove(ContractNumber contractNumber)
    {
        objectStore.remove(contractNumber);
    }


    @Override
    public List<Contract> getByAdvisor(String advisor)
    {
        return objectStore
                .getStringQuery(ADVISOR, String.class)
                .isEqualTo(advisor);
    }

    @Override
    public Contract get(ContractNumber contractNumber)
    {
        return objectStore
                .get(contractNumber)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Contract> getAll()
    {
        return objectStore.get();
    }

    @Override
    public List<Contract> getSignedContracts()
    {
        return objectStore
                .getNumericQuery(CONTRACT_SIGNED, Boolean.class)
                .isEqualTo(true);
    }

    @Override
    public List<Contract> getUnsignedContracts()
    {
        return objectStore
                .getNumericQuery(CONTRACT_SIGNED, Boolean.class)
                .isEqualTo(false);
    }

    @Override
    public Optional<Contract> getHighestContractNumber()
    {
        return objectStore
                .getNumericQuery(CONTRACT_NUMBER, Integer.class)
                .getDescending(1)
                .stream()
                .findFirst();
    }

}
