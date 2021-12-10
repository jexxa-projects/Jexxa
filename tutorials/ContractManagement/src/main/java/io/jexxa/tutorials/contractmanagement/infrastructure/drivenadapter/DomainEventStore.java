package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.tutorials.contractmanagement.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.contractmanagement.domainservice.IDomainEventStore;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.instantTag;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numberTag;

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
    public enum DomainEventSchema implements MetadataSchema
    {
        CONTRACT_NUMBER(numberTag((domainEvent -> domainEvent.getContractNumber().getValue())) ),

        SIGNATURE_DATE(instantTag(ContractSigned::getSignatureDate));

        // The remaining code is always the same for all metadata specifications
        private final MetaTag<ContractSigned, ?, ? > metaTag;

        DomainEventSchema(MetaTag<ContractSigned,?, ?> metaTag)
        {
            this.metaTag = metaTag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public MetaTag<ContractSigned, ?, ?> getTag()
        {
            return metaTag;
        }
    }

    private final IObjectStore<ContractSigned, ContractNumber, DomainEventSchema> objectStore;


    public DomainEventStore(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(ContractSigned.class, ContractSigned::getContractNumber, DomainEventSchema.class, properties);
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
                .getNumericQuery(DomainEventSchema.SIGNATURE_DATE, Instant.class)
                .getRangeClosed(startTime, endTime);
    }

    @Override
    public List<ContractSigned> get()
    {
        return objectStore.get();
    }
}
