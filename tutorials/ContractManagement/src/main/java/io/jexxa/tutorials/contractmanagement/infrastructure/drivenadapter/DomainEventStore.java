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
     * Here we define the values to query DomainEvents. The domain events should be queried by following information:
     * <ol>
     *  <li>Contract number</li>
     *  <li>Date of the signature</li>
     * </ol>
     */
    public enum DomainEventMetadata implements MetadataComparator
    {
        CONTRACT_NUMBER(numberComparator((domainEvent -> domainEvent.getContractNumber().getValue())) ),
        SIGNATURE_DATE(instantComparator(ContractSigned::getSignatureDate));

        // The following code is always the same and required to use by the ObjectStore
        private final Comparator<ContractSigned, ?, ? > comparator;
        DomainEventMetadata(Comparator<ContractSigned,?, ?> comparator)
        {
            this.comparator = comparator;
        }
        @Override @SuppressWarnings("unchecked") public Comparator<ContractSigned, ?, ?> getComparator()
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
