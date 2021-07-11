package io.jexxa.tutorials.domaineventstore.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.instantComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.keyComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.numberComparator;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparators.valueComparator;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.MetadataComparator;
import io.jexxa.tutorials.domaineventstore.domain.domainevent.MyDomainEvent;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.BatchNumber;
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

        WEIGHT_VALUE(numberComparator(( domainEvent -> domainEvent.getBatchNumber().getValue())) ),

        TIMESTAMP_VALUE(instantComparator(MyDomainEvent::getTimestamp));

        private final Comparator<MyDomainEvent, ? > comparator;

        DomainEventMetadata(Comparator<MyDomainEvent,?> comparator)
        {
            this.comparator = comparator;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparator<MyDomainEvent, ?> getComparator()
        {
            return comparator;
        }
    }

    private final IObjectStore<MyDomainEvent, String, DomainEventMetadata> objectStore;


    public DomainEventStore(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(MyDomainEvent.class, MyDomainEvent::getUuid, DomainEventMetadata.class, properties);
    }

    @Override
    public void add(MyDomainEvent domainEvent)
    {
        objectStore.add(domainEvent);
    }

    @Override
    public List<MyDomainEvent> get(Instant startTime, Instant endTime)
    {
        return objectStore
                .getIQuery(DomainEventMetadata.TIMESTAMP_VALUE)
                .getRangeClosed(startTime, endTime);
    }

    @Override
    public List<MyDomainEvent> getBatchNumbersLessThan(BatchNumber batchNumber)
    {
        return objectStore
                .getIQuery(DomainEventMetadata.WEIGHT_VALUE)
                .getLessThan(batchNumber);
    }

    @Override
    public List<MyDomainEvent> getLatestEvents(int number)
    {
        return objectStore
                .getIQuery(DomainEventMetadata.TIMESTAMP_VALUE)
                .getDescending(number);
    }
}
