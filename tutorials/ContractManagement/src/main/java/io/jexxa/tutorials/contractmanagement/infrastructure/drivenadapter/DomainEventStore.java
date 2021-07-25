package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.instantComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparators.numberComparator;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.tutorials.contractmanagement.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.contractmanagement.domainservice.IDomainEventStore;

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
        CONTRACT_NUMBER(numberComparator((domainEvent -> domainEvent.getContractNumber().getValue())) ),

        SIGNATURE_DATE(instantComparator(ContractSigned::getSignatureDate));

        private final Comparator<ContractSigned, ?, ? > comparator;

        DomainEventMetadata(Comparator<ContractSigned,?, ?> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<ContractSigned, ?, ?> getComparator()
        {
            return comparator;
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
                .getNumericQuery(DomainEventMetadata.SIGNATURE_DATE, Instant.class)
                .getRangeClosed(startTime, endTime);
    }

    @Override
    public List<ContractSigned> get()
    {
        return objectStore.get();
    }
}
