package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.StringComparator;

class IMDBStringQuery<T, K, S> implements IStringQuery<T, S>
{
    StringComparator<T, S> stringComparator;
    Map<K, T> internalMap;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBStringQuery(Map<K, T> internalMap, StringComparator<T, S> stringComparator)
    {
        this.internalMap = internalMap;
        this.stringComparator = stringComparator;
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
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(element -> stringComparator.convertAggregate(element)))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> stringComparator.compareToAggregate(element1, element2))
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
