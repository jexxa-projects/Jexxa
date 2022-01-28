package io.jexxa.tutorials.contractmanagement.domain.domainevent;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.tutorials.contractmanagement.domain.valueobject.ContractNumber;

import java.time.Instant;

@DomainEvent
public class ContractSigned
{
    private final ContractNumber contractNumber;
    private final Instant signatureDate;

    public ContractSigned(ContractNumber contractNumber, Instant signatureDate)
    {
        this.contractNumber = contractNumber;
        this.signatureDate = signatureDate;
    }

    public ContractNumber getContractNumber()
    {
        return contractNumber;
    }

    public Instant getSignatureDate()
    {
        return signatureDate;
    }
}
