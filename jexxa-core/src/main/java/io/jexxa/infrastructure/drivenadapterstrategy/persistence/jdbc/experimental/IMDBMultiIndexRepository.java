package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;

public class IMDBMultiIndexRepository<T, K, M extends Enum<M> & SearchStrategy>  extends IMDBRepository<T, K> implements IMultiIndexRepository<T, K, M>
{

    Set<M> comparatorFunctions;


    public IMDBMultiIndexRepository(
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
    public <S> ISubset<T, S> getSubset(M strategy)
    {
        if ( !comparatorFunctions.contains(strategy) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDSubset<>(getOwnAggregateMap(), strategy.get());
    }

    public static class IMBDSubset<T, K, S> implements ISubset<T, S>
    {
        RangeComparator<T, S> rangeComparator;
        Map<K, T> internalMap;


        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDSubset(Map<K, T> internalMap, RangeComparator<T,S> rangeComparator)
        {
            this.internalMap = internalMap;
            this.rangeComparator = rangeComparator;
        }

        @Override
        public List<T> getFrom(S startValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(element -> rangeComparator.getIntValueT(element).compareTo( rangeComparator.getIntValueS(startValue)) >= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(t -> rangeComparator.getIntValueT(t).compareTo( rangeComparator.getIntValueS(startValue)) >= 0)
                    .filter(t -> rangeComparator.getIntValueT(t).compareTo( rangeComparator.getIntValueS(endValue)) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getUntil(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(t -> rangeComparator.getIntValueT(t).compareTo( rangeComparator.getIntValueS(endValue)) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getAscending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(element -> rangeComparator.getIntValueT(element)))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getDescending(int amount)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .sorted((element1, element2) -> rangeComparator.getIntValueT(element2).compareTo( rangeComparator.getIntValueT(element1)))
                    .limit(amount)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> get(S value)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(t -> rangeComparator.getIntValueT(t).compareTo( rangeComparator.getIntValueS(value)) == 0)
                    .collect(Collectors.toList());
        }

    }
}

