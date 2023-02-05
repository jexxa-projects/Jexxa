package io.jexxa.adapterapi.invocation.transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final TransactionManager INSTANCE = new TransactionManager();

    private final List<TransactionHandler> transactionHandlerList = new ArrayList<>();
    public static TransactionManager getInstance()
    {
        return INSTANCE;
    }

    public static void addTransactionHandler(TransactionHandler transactionHandler)
    {
        INSTANCE.transactionHandlerList.add(transactionHandler);
    }

    public static void initTransaction()
    {
        System.out.println("Init Transaction");
        INSTANCE.transactionHandlerList.forEach(TransactionHandler::initTransaction);
    }

    public static void closeTransaction()
    {
        INSTANCE.transactionHandlerList.forEach(TransactionHandler::closeTransaction);
        System.out.println("Closed Transaction");
    }


}
