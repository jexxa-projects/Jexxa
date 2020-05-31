package io.jexxa.infrastructure.drivenadapterstrategy.persistence.imdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;

/**
 */
@SuppressWarnings("unused")
public class IMDBRepository<T, K>  implements IRepository<T, K>
{
    // Each IMDB repository is represented by a map for a specific type.
    private static final Map< Class<?>, Map<?,?> > REPOSITORY_MAP = new ConcurrentHashMap<>();


    private final Map<K, T> aggregateMap;
    private final Function<T,K> keyFunction;

    @SuppressWarnings("java:S1172")
    public IMDBRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        aggregateMap = getAggregateMap(aggregateClazz);
        this.keyFunction = keyFunction;
    }

    @Override
    public void update(T aggregate)
    {
        // Nothing to do here because operations are performed on the aggregate 
    }

    @Override
    public void remove(K key)
    {
        aggregateMap.remove( key );
    }

    @Override
    public void removeAll()
    {
        aggregateMap.clear();
    }

    @Override
    public void add(T aggregate)
    {
        if (aggregateMap.containsKey( keyFunction.apply(aggregate)))
        {
            throw new IllegalArgumentException("An object with given key already exists");
        }
        aggregateMap.put(keyFunction.apply(aggregate), aggregate);
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        return Optional.ofNullable(aggregateMap.get( primaryKey));
    }


    @Override
    public List<T> get()
    {
        return new ArrayList<>(aggregateMap.values());
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T, K> Map<T, K> getAggregateMap(Class<?> aggregateClazz)
    {
        if ( REPOSITORY_MAP.containsKey(aggregateClazz) )
        {
            return (Map<T, K>) REPOSITORY_MAP.get(aggregateClazz);
        }

        var newRepository = new ConcurrentHashMap<T,K>();
        REPOSITORY_MAP.put(aggregateClazz, newRepository);
        return newRepository;
    }
}
