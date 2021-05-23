package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Supplier;

public class RangeComparator<T, S>
{
    private final Function<T, BigDecimal> converterFunctionT;
    private final Function<S, BigDecimal> converterFunctionS;

    public RangeComparator(Function<T, ? extends Number> converterFunctionT,
                           Function<S, ? extends Number> converterFunctionS)
    {
        this.converterFunctionT = aggregate -> new BigDecimal( converterFunctionT.apply(aggregate).toString() );
        this.converterFunctionS = parameter -> new BigDecimal( converterFunctionS.apply(parameter).toString());
    }

    public <U extends Number, V extends Number> RangeComparator(Supplier<U> supplierT,
                                                                Supplier<V> supplierS)
    {
        this.converterFunctionT = aggregate -> new BigDecimal ( supplierT.get().toString());
        this.converterFunctionS = element -> new BigDecimal( supplierS.get().toString());
    }


    public BigDecimal getIntValueT(T aggregate)
    {
        return converterFunctionT.apply(aggregate);
    }

    public BigDecimal getIntValueS(S aggregate)
    {
        return converterFunctionS.apply(aggregate);
    }

}

