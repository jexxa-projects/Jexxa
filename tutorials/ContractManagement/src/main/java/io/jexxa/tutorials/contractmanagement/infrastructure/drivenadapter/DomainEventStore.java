package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.instantTag;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numberTag;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.Metadata;
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
    public enum DomainEventTags implements Metadata
    {
        CONTRACT_NUMBER(numberTag((domainEvent -> domainEvent.getContractNumber().getValue())) ),

        SIGNATURE_DATE(instantTag(ContractSigned::getSignatureDate));

        // The remaining code is always the same for all metadata specifications
        private final MetaTag<ContractSigned, ?, ? > metaTag;

        DomainEventTags(MetaTag<ContractSigned,?, ?> metaTag)
        {
            this.metaTag = metaTag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public MetaTag<ContractSigned, ?, ?> getMetaTag()
        {
            return metaTag;
        }
    }

    private final IObjectStore<ContractSigned, ContractNumber, DomainEventTags> objectStore;


    public DomainEventStore(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(ContractSigned.class, ContractSigned::getContractNumber, DomainEventTags.class, properties);
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
                .getNumericQuery(DomainEventTags.SIGNATURE_DATE, Instant.class)
                .getRangeClosed(startTime, endTime);
    }

    @Override
    public List<ContractSigned> get()
    {
        return objectStore.get();
    }
}
