package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.StringComparator;

class IMDBStringQuery<T, K, S> implements IStringQuery<T, S>
{
    private final StringComparator<T, S> stringComparator;
    private final Map<K, T> internalMap;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBStringQuery(Map<K, T> internalMap, StringComparator<T, S> stringComparator, Class<S> queryType)
    {
        this.internalMap = internalMap;
        this.stringComparator = stringComparator;
        Objects.requireNonNull( queryType );//Type required for java type inference
    }


    @Override
    public List<T> beginsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .filter( element -> stringComparator.convertAggregate(element).startsWith(stringComparator.convertValue(value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> endsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .filter( element -> stringComparator.convertAggregate(element).endsWith(stringComparator.convertValue(value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> includes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .filter( element -> stringComparator.convertAggregate(element).contains(stringComparator.convertValue(value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .filter( element -> stringComparator.convertAggregate(element).equals(stringComparator.convertValue(value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> notIncludes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .filter( element -> !stringComparator.convertAggregate(element).contains(stringComparator.convertValue(value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNotNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element) != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(stringComparator::convertAggregate))
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

    protected int typeSpecificCompareTo(String value1, String value2)
    {
        return value1.compareTo(value2);
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

        var aggregateValue1 = stringComparator.convertAggregate(aggregate1);
        var aggregateValue2 = stringComparator.convertAggregate(aggregate2);

        if ( aggregateValue1 == null && aggregateValue2 == null)
        {
            return 0;
        } else if ( aggregateValue1 == null)
        {
            return 1;
        } else if ( aggregateValue2 == null)
        {
            return 1;
        }

        return typeSpecificCompareTo( aggregateValue1, aggregateValue2);
    }
}
