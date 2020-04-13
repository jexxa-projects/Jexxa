package io.ddd.jexxa.infrastructure.drivenadapter.persistence.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import io.ddd.jexxa.infrastructure.drivenadapter.persistence.IRepositoryConnection;

@SuppressWarnings("unused")
public class InMemoryRepository<T, K>  implements IRepositoryConnection<T, K>
{

    final Map<K, T> aggregateMap;
    final Function<T,K> keyFunction;


    public InMemoryRepository(Class<T> aggregateClazz, Class<K> keyClazz, Function<T,K> keyFunction, Properties properties)
    {
        aggregateMap = new HashMap<>();
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
}
