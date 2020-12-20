package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb.IMDBRepository;

public class IMDBComparableRepository<T, K, M extends Enum<M> & Strategy>  extends IMDBRepository<T, K> implements IComparableRepository<T, K, M>
{

    Set<M> comparatorFunctions;


    public IMDBComparableRepository(Class<T> aggregateClazz,
                                    Function<T, K> keyFunction,
                                    Set<M> comparatorFunctions,
                                    Properties properties)
    {
        super(aggregateClazz, keyFunction, properties);
        this.comparatorFunctions = comparatorFunctions;
    }

    @Override
    public <S> IRangedResult<T, S> getRangeInterface(M strategy)
    {
        if ( !comparatorFunctions.contains(strategy) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new IMBDRangedResult<>(getOwnAggregateMap(), strategy.getStrategy());
    }

    public static class IMBDRangedResult<T, K, S> implements IRangedResult<T, S>
    {
        Comparator <T, S> comparator;
        Map<K, T> internalMap;


        private Map<K, T> getOwnAggregateMap()
        {
            return internalMap;
        }

        public IMBDRangedResult(Map<K, T> internalMap, Comparator<T,S> comparator)
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
                    .filter(element -> comparator.getIntValueT(element).compareTo( comparator.getIntValueS(startValue)) >= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(t -> comparator.getIntValueT(t).compareTo( comparator.getIntValueS(startValue)) >= 0)
                    .filter(t -> comparator.getIntValueT(t).compareTo( comparator.getIntValueS(endValue)) <= 0)
                    .collect(Collectors.toList());
        }

        @Override
        public List<T> getUntil(S endValue)
        {
            return getOwnAggregateMap()
                    .values()
                    .stream()
                    .filter(t -> comparator.getIntValueT(t).compareTo( comparator.getIntValueS(endValue)) <= 0)
                    .collect(Collectors.toList());
        }

    }
}

