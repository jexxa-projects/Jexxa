package io.jexxa.tutorials.domaineventstore.domain.aggregate;

import java.time.Instant;
import java.util.Objects;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateFactory;
import io.jexxa.addend.applicationcore.AggregateID;
import io.jexxa.tutorials.domaineventstore.domain.domainevent.ContractSigned;
import io.jexxa.tutorials.domaineventstore.domain.valueobject.ContractNumber;

@Aggregate
public class Contract
{
    private final ContractNumber contractNumber;
    private String advisor;
    private boolean isTerminated;

    private Contract(ContractNumber contractNumber, String advisor)
    {
        this.contractNumber = Objects.requireNonNull( contractNumber );
        this.advisor = advisor;
        this.isTerminated = false;
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

    public void terminate()
    {
        this.isTerminated = true;
    }
    public boolean isTerminated()
    {
        return isTerminated;
    }


    public ContractSigned sign()
    {
        return new ContractSigned(contractNumber, Instant.now());
    }

    @AggregateFactory(Contract.class)
    public static Contract newContract(ContractNumber contractNumber, String advisor)
    {
        return new Contract(contractNumber, advisor);
    }
}
