package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

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


    public IMDBMultiIndexRepository(Class<T> aggregateClazz,
                                    Function<T, K> keyFunction,
                                    Set<M> comparatorFunctions,
                                    Properties properties)
    {
        super(aggregateClazz, keyFunction, properties);
        this.comparatorFunctions = comparatorFunctions;
    }

    @Override
    public <S> IRangeQuery<T, S> getRangeQuery(M strategy)
    {
        if ( !comparatorFunctions.contains(strategy) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDRangeQuery<>(getOwnAggregateMap(), strategy.get());
    }

    public static class IMBDRangeQuery<T, K, S> implements IRangeQuery<T, S>
    {
        RangeComparator<T, S> rangeComparator;
        Map<K, T> internalMap;


        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDRangeQuery(Map<K, T> internalMap, RangeComparator<T,S> rangeComparator)
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

    }
}

