package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

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
        return null;
    }

    @Override
    public List<T> includes(S value)
    {
        return null;
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return null;
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return null;
    }

    @Override
    public List<T> getAscending()
    {
        return null;
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return null;
    }

    @Override
    public List<T> getDescending()
    {
        return null;
    }
}
