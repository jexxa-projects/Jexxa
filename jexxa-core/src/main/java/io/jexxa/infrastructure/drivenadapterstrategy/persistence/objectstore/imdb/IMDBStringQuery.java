package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.StringComparator;

class IMDBStringQuery<T, K, S> implements IStringQuery<T, S>
{
    private final StringComparator<T, S> stringComparator;
    private final Map<K, T> internalMap;
    @SuppressWarnings("unused") // Type required for java type inference
    private final Class<S> queryType;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBStringQuery(Map<K, T> internalMap, StringComparator<T, S> stringComparator, Class<S> queryType)
    {
        this.internalMap = internalMap;
        this.stringComparator = stringComparator;
        this.queryType = queryType;
    }


    @Override
    public List<T> beginsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element).startsWith(value.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> endsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element).endsWith(value.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> includes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element).contains(value.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringComparator.convertAggregate(element).equals(value.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> notIncludes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> !stringComparator.convertAggregate(element).contains(value.toString()))
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
                .sorted(stringComparator::compareToAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> stringComparator.compareToAggregate(element2, element1))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> stringComparator.compareToAggregate(element2, element1))
                .collect(Collectors.toList());
    }
}
