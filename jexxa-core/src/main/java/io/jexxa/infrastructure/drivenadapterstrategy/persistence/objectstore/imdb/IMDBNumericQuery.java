package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.NumericTag;

class IMDBNumericQuery<T, K, S> implements INumericQuery<T, S>
{
    private final NumericTag<T, S> numericTag;
    private final Map<K, T> internalMap;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBNumericQuery(Map<K, T> internalMap, NumericTag<T, S> numericTag, Class<S> queryType)
    {
        this.internalMap = internalMap;
        this.numericTag = numericTag;
        Objects.requireNonNull( queryType );//Type required for java type inference
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, startValue) >= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, value) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getRangeClosed(S startValue, S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, startValue) >= 0)
                .filter(element -> compareToValue(element, endValue) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getRange(S startValue, S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, startValue) >= 0)
                .filter(element -> compareToValue(element, endValue) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isLessOrEqualThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, endValue) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isLessThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, endValue) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(this::compareToAggregate)
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(this::compareToAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> compareToValue(element, value) == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNotEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> compareToValue(element, value) != 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNotNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .collect(Collectors.toList());
    }

    private int compareToValue(T aggregate, S value)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(value);

        if(numericTag.getFromAggregate(aggregate) == null)
        {
            return 1;
        }

        var aggregateValue = numericTag.getFromAggregate(aggregate);

        return typeSpecificCompareTo(aggregateValue, numericTag.getFromValue(value));
    }

    protected int typeSpecificCompareTo(Number value1, Number value2)
    {
        //Handle both != null
        var aggregateValue1BD = new BigDecimal( value1.toString() );
        var aggregateValue2BD = new BigDecimal( value2.toString() );
        return aggregateValue1BD.compareTo(aggregateValue2BD);
    }
    /**
     * Compares the value of the two aggregates which each other
     *
     * @param aggregate1 first aggregate
     * @param aggregate2 second aggregate
     * @return 0 If the value of aggregate1 is equal to value aggregate2 <br>
     *     -1 if value of aggregate1 &lt; value of aggregate2 <br>
     *     1 if value of aggregate1 &gt; value of aggregate2 <br>
     */
    private int compareToAggregate(T aggregate1, T aggregate2)
    {
        Objects.requireNonNull(aggregate1);
        Objects.requireNonNull(aggregate2);

        var aggregateValue1 = numericTag.getFromAggregate(aggregate1);
        var aggregateValue2 = numericTag.getFromAggregate(aggregate2);

        if ( aggregateValue1 == null && aggregateValue2 == null)
        {
            return 0;
        } else if ( aggregateValue1 == null || aggregateValue2 == null)
        {
            return 1;
        }

        return typeSpecificCompareTo( aggregateValue1, aggregateValue2);
    }
}
