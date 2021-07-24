package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

public enum SQLOrder
{
    ASC ("ASC"),
    DESC ("DESCT"),
    ASC_NULL_LAST("ASC NULLS LAST"),
    DESC_NULL_LAST("DESC NULLS LAST");

    private final String orderName;

    SQLOrder(String orderName)
    {
        this.orderName = orderName;
    }

    public String getOrderName()
    {
        return orderName;
    }

}
