package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;

public class IMDBMultiValueRepository<T, K, M extends Enum<M> & SchemaComparator>  extends IMDBRepository<T, K> implements IMultiValueRepository<T, K, M>
{
    private final Set<M> comparatorFunctions;

    public IMDBMultiValueRepository(
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
    public <S> IQuery<T, S> getIQuery(M strategy)
    {
        if ( !comparatorFunctions.contains(strategy) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDQuery<>(getOwnAggregateMap(), strategy.getComparator());
    }

    public static class IMBDQuery<T, K, S> implements IQuery<T, S>
    {
        Comparator<T, S> comparator;
        Map<K, T> internalMap;

        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDQuery(Map<K, T> internalMap, Comparator<T, S> comparator)
        {
            this.internalMap = internalMap;
            this.comparator = comparator;
        }

        @Override
        public List<T> getFrom(S startValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, startValue) >= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, startValue) >= 0)
                    .filter(element -> comparator.compareToValue(element, endValue) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getUntil(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> comparator.compareToValue(element, endValue) <= 0)
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
        public List<T> get(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element-> comparator.compareToValue(element, value) == 0)
                    .collect(Collectors.toList());
        }

    }




}

