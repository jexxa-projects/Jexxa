package io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.booleanTag;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.numberTag;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTags.stringTag;
import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadataSchema.ADVISOR;
import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadataSchema.CONTRACT_NUMBER;
import static io.jexxa.tutorials.contractmanagement.infrastructure.drivenadapter.ContractRepository.ContractMetadataSchema.CONTRACT_SIGNED;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.tutorials.contractmanagement.domain.aggregate.Contract;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;
import io.jexxa.tutorials.contractmanagement.domainservice.IContractRepository;

@SuppressWarnings("unused")
public class ContractRepository  implements IContractRepository
{
    /**
     * Here we define the values to query contracts. Apart from their key, elements should be queried by following information: <br>
     * <ol>
     *    <li>Contract number</li>
     *    <li>Contract signed flag</li>
     *    <li>Advisor of the contract</li>
     * </ol>
     */
    enum ContractMetadataSchema implements MetadataSchema
    {
        CONTRACT_NUMBER(numberTag(element -> element.getContractNumber().getValue())),

        CONTRACT_SIGNED(booleanTag(Contract::isSigned)),

        ADVISOR(stringTag(Contract::getAdvisor));

        // The remaining code is always the same for all metadata specifications
        private final MetaTag<Contract, ?, ? > metaTag;

        ContractMetadataSchema(MetaTag<Contract,?, ?> metaTag)
        {
            this.metaTag = metaTag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public MetaTag<Contract, ?, ?> getTag()
        {
            return metaTag;
        }
    }


    private final IObjectStore<Contract, ContractNumber, ContractMetadataSchema> objectStore;

    public ContractRepository(Properties properties)
    {
        this.objectStore = ObjectStoreManager.getObjectStore(Contract.class, Contract::getContractNumber, ContractMetadataSchema.class, properties);
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
