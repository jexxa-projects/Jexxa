package io.ddd.jexxa.infrastructure.drivenadapter.persistence.imdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.ddd.jexxa.infrastructure.drivenadapter.persistence.IRepositoryConnection;

/**
 * TODO: Add an option to serialize data on disk 
 */
@SuppressWarnings("unused")
public class IMDBConnection<T, K>  implements IRepositoryConnection<T, K>
{
    private static final Map< Class<?>, Map<?,?> > repositoryMap = new ConcurrentHashMap<>();


    final Map<K, T> aggregateMap;
    final Function<T,K> keyFunction;

    @SuppressWarnings("java:S1172")
    public IMDBConnection(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
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
        if ( repositoryMap.containsKey(aggregateClazz) )
        {
            return (Map<T, K>) repositoryMap.get(aggregateClazz);
        }

        var newRepository = new ConcurrentHashMap<T,K>();
        repositoryMap.put(aggregateClazz, newRepository);
        return newRepository;
    }
}
