package io.jexxa.common.wrapper.jdbc.builder;

public enum SQLOrder
{
    ASC ("ASC"),
    DESC ("DESC"),
    ASC_NULLS_LAST("ASC NULLS LAST"),
    DESC_NULLS_LAST("DESC NULLS LAST");

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
