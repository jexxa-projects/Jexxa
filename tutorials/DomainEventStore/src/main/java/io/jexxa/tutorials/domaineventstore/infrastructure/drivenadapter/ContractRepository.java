package io.jexxa.tutorials.domaineventstore.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.valueComparator;
import static io.jexxa.tutorials.domaineventstore.infrastructure.drivenadapter.ContractRepository.ContractMetadata.CONTRACT_TERMINATED;

import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.tutorials.domaineventstore.domain.aggregate.Contract;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.domaineventstore.domainservice.IContractRepository;

public class ContractRepository  implements IContractRepository
{
    enum ContractMetadata implements MetadataComparator
    {
        KEY(keyComparator()),

        VALUE(valueComparator()),

        CONTRACT_TERMINATED(Comparators.booleanComparator(Contract::isTerminated));

        //ADVISOR()


        private final NumericComparator<Contract, ? > numericComparator;

        ContractMetadata(NumericComparator<Contract,?> numericComparator)
        {
            this.numericComparator = numericComparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public NumericComparator<Contract, ?> getComparator()
        {
            return numericComparator;
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
    public void remove(ContractNumber contractNumber)
    {
        objectStore.remove(contractNumber);
    }

    @Override
    public List<Contract> getTerminated()
    {
        return objectStore
                .getNumericQuery(CONTRACT_TERMINATED, Boolean.class)
                .isEqualTo(true);
    }

    @Override
    public List<Contract> getByAdvisor(String advisor)
    {
        return null;
    }

    @Override
    public Contract get(ContractNumber contractNumber)
    {
        return objectStore.get(contractNumber).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Contract> getAll()
    {
        return objectStore.get();
    }

}
