package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Supplier;

public class RangeComparator<T, S>
{
    private final Function<T, BigDecimal> toNumberFunctionT;
    private final Function<S, BigDecimal> toNumberFunctionS;

    public RangeComparator(Function<T, ? extends Number> toNumberFunctionT,
                           Function<S, ? extends Number> toNumberFunctionS)
    {
        this.toNumberFunctionT = aggregate -> new BigDecimal( toNumberFunctionT.apply(aggregate).toString() );
        this.toNumberFunctionS = parameter -> new BigDecimal( toNumberFunctionS.apply(parameter).toString());
    }

    public <U extends Number, V extends Number> RangeComparator(Supplier<U> supplierT,
                                                                Supplier<V> supplierS)
    {
        this.toNumberFunctionT = aggregate -> new BigDecimal ( supplierT.get().toString());
        this.toNumberFunctionS = element -> new BigDecimal( supplierS.get().toString());
    }


    public BigDecimal getIntValueT(T aggregate)
    {
        return toNumberFunctionT.apply(aggregate);
    }

    public BigDecimal getIntValueS(S aggregate)
    {
        return toNumberFunctionS.apply(aggregate);
    }

}

