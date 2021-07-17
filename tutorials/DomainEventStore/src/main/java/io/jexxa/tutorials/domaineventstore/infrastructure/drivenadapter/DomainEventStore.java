package io.jexxa.tutorials.domaineventstore.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.instantComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.valueComparator;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.MetadataComparator;
import io.jexxa.tutorials.domaineventstore.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.domaineventstore.domainservice.IDomainEventStore;

@SuppressWarnings("unused")
public class DomainEventStore implements IDomainEventStore
{

    /**
     * In this Schema we define the values that we use to query DomainEvents
     *
     * Conventions for JDBC databases:
     * - Enum name is used for the name of the row so that there is a direct mapping between the strategy and the database
     * - Adding a new strategy in code after initial usage requires that the database is extended in some woy
     */
    public enum DomainEventMetadata implements MetadataComparator
    {
        KEY(keyComparator()),

        VALUE(valueComparator()),

        CONTRACT_NUMBER(numberComparator((domainEvent -> domainEvent.getContractNumber().getValue())) ),

        SIGNATURE_DATE(instantComparator(ContractSigned::getSignatureDate));

        private final NumericComparator<ContractSigned, ? > numericComparator;

        DomainEventMetadata(NumericComparator<ContractSigned,?> numericComparator)
        {
            this.numericComparator = numericComparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public NumericComparator<ContractSigned, ?> getComparator()
        {
            return numericComparator;
        }
    }

    private final IObjectStore<ContractSigned, ContractNumber, DomainEventMetadata> objectStore;


    public DomainEventStore(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(ContractSigned.class, ContractSigned::getContractNumber, DomainEventMetadata.class, properties);
    }

    @Override
    public void add(ContractSigned domainEvent)
    {
        objectStore.add(domainEvent);
    }

    @Override
    public List<ContractSigned> get(Instant startTime, Instant endTime)
    {
        return objectStore
                .getNumericQuery(DomainEventMetadata.SIGNATURE_DATE)
                .getRangeClosed(startTime, endTime);
    }

    @Override
    public List<ContractSigned> getBatchNumbersLessThan(ContractNumber contractNumber)
    {
        return objectStore
                .getNumericQuery(DomainEventMetadata.CONTRACT_NUMBER)
                .getLessThan(contractNumber);
    }

    @Override
    public List<ContractSigned> getLatestBatches(int number)
    {
        return objectStore
                .getNumericQuery(DomainEventMetadata.CONTRACT_NUMBER)
                .getDescending(number);
    }
}
