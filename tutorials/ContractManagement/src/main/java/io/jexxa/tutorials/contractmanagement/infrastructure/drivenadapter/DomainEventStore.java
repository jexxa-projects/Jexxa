package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.Converters.instantConverter;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.Converters.numberConverter;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.Converter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.MetadataConverter;
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
    public enum DomainEventMetadata implements MetadataConverter
    {
        CONTRACT_NUMBER(numberConverter((domainEvent -> domainEvent.getContractNumber().getValue())) ),
        SIGNATURE_DATE(instantConverter(ContractSigned::getSignatureDate));

        // The following code is always the same and required to use by the ObjectStore
        private final Converter<ContractSigned, ?, ? > converter;
        DomainEventMetadata(Converter<ContractSigned,?, ?> converter)
        {
            this.converter = converter;
        }
        @Override @SuppressWarnings("unchecked") public Converter<ContractSigned, ?, ?> getValueConverter()
        {
            return converter;
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
