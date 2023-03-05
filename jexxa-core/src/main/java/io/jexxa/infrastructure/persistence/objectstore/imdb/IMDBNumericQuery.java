package io.jexxa.infrastructure.persistence.objectstore.imdb;

import io.jexxa.infrastructure.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.persistence.objectstore.metadata.NumericTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                .toList();
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, value) > 0)
                .toList();
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
                .toList();
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
                .toList();
    }

    @Override
    public List<T> isLessOrEqualThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, endValue) <= 0)
                .toList();
    }

    @Override
    public List<T> isLessThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .filter(element -> compareToValue(element, endValue) < 0)
                .toList();
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(this::compareToAggregate)
                .limit(amount)
                .toList();
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(this::compareToAggregate)
                .toList();
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .limit(amount)
                .toList();
    }

    @Override
    public List<T> getDescending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .toList();
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> compareToValue(element, value) == 0)
                .toList();
    }

    @Override
    public List<T> isNotEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> compareToValue(element, value) != 0)
                .toList();
    }

    @Override
    public List<T> isNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) == null)
                .toList();
    }

    @Override
    public List<T> isNotNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> numericTag.getFromAggregate(element) != null)
                .toList();
    }

    private int compareToValue(T aggregate, S value)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(value);

        var aggregateValue = numericTag.getFromAggregate(aggregate);

        if(aggregateValue == null)
        {
            return 1;
        }

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
     *     1 if one of the given values is null <br>
     */
    @SuppressWarnings("DuplicatedCode")
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
