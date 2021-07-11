package io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.MetadataComparator;

public class IMDBObjectStore<T, K, M extends Enum<M> & MetadataComparator>  extends IMDBRepository<T, K> implements IObjectStore<T, K, M>
{
    private final Set<M> comparatorFunctions;

    public IMDBObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> comparatorSchema,
            Properties properties
            )
    {
        super(aggregateClazz, keyFunction, properties);
        this.comparatorFunctions = EnumSet.allOf(comparatorSchema);
    }

    @Override
    public <S> IObjectQuery<T, S> getObjectQuery(M metadata)
    {
        if ( !comparatorFunctions.contains(metadata) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDObjectQuery<>(getOwnAggregateMap(), metadata.getComparator());
    }

    public static class IMBDObjectQuery<T, K, S> implements IObjectQuery<T, S>
    {
        Comparator<T, S> comparator;
        Map<K, T> internalMap;

        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDObjectQuery(Map<K, T> internalMap, Comparator<T, S> comparator)
        {
            this.internalMap = internalMap;
            this.comparator = comparator;
        }

        @Override
        public List<T> getGreaterOrEqualThan(S startValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, startValue) >= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getGreaterThan(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, value) > 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRangeClosed(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, startValue) >= 0)
                    .filter(element -> comparator.compareToValue(element, endValue) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, startValue) >= 0)
                    .filter(element -> comparator.compareToValue(element, endValue) < 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getLessOrEqualThan(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, endValue) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getLessThan(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, endValue) < 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getAscending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> comparator.compareToAggregate(element1, element2))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getDescending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> comparator.compareToAggregate(element2, element1))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getEqualTo(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element-> comparator.compareToValue(element, value) == 0)
                    .collect(Collectors.toList());
        }

    }




}

