package io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.NumericComparator;
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
    public <S> INumericQuery<T, S> getObjectQuery(M metadata)
    {
        if ( !comparatorFunctions.contains(metadata) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDNumericQuery<>(getOwnAggregateMap(), metadata.getComparator());
    }

    public static class IMBDNumericQuery<T, K, S> implements INumericQuery<T, S>
    {
        NumericComparator<T, S> numericComparator;
        Map<K, T> internalMap;

        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDNumericQuery(Map<K, T> internalMap, NumericComparator<T, S> numericComparator)
        {
            this.internalMap = internalMap;
            this.numericComparator = numericComparator;
        }

        @Override
        public List<T> getGreaterOrEqualThan(S startValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getGreaterThan(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, value) > 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRangeClosed(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                    .filter(element -> numericComparator.compareToValue(element, endValue) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                    .filter(element -> numericComparator.compareToValue(element, endValue) < 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getLessOrEqualThan(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, endValue) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getLessThan(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> numericComparator.compareToValue(element, endValue) < 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getAscending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> numericComparator.compareToAggregate(element1, element2))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getAscending()
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> numericComparator.compareToAggregate(element1, element2))
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getDescending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> numericComparator.compareToAggregate(element2, element1))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getDescending()
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> numericComparator.compareToAggregate(element2, element1))
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getEqualTo(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element-> numericComparator.compareToValue(element, value) == 0)
                    .collect(Collectors.toList());
        }

    }




}

