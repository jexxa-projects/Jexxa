package io.jexxa.tutorials.contractmanagement.domain.aggregate;

import java.time.Instant;
import java.util.Objects;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateFactory;
import io.jexxa.addend.applicationcore.AggregateID;
import io.jexxa.tutorials.contractmanagement.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;

@Aggregate
public class Contract
{
    private final ContractNumber contractNumber;
    private String advisor;
    private boolean isSigned;

    private Contract(ContractNumber contractNumber, String advisor)
    {
        this.contractNumber = Objects.requireNonNull( contractNumber );
        this.advisor = Objects.requireNonNull( advisor );
        this.isSigned = false;
    }

    @AggregateID
    public ContractNumber getContractNumber()
    {
        return contractNumber;
    }

    public String getAdvisor()
    {
        return advisor;
    }

    public void setAdvisor(String advisor)
    {
        this.advisor = advisor;
    }

    public ContractSigned sign()
    {
        isSigned = true;
        return new ContractSigned(contractNumber, Instant.now());
    }

    public boolean isSigned()
    {
        return isSigned;
    }

    @AggregateFactory(Contract.class)
    public static Contract newContract(ContractNumber contractNumber, String advisor)
    {
        return new Contract(contractNumber, advisor);
    }
}
