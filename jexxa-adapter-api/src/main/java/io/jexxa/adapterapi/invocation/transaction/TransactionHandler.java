package io.jexxa.adapterapi.invocation.transaction;

public interface TransactionHandler {
    void initTransaction();
    void closeTransaction();
}
