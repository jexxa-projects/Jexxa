package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class InMemoryDB<T, K>  implements IDBConnection<T>
{

    final Map<K, T> aggregateMap;
    final Function<T,K> keyFunction;


    public InMemoryDB(Function<T,K> keyFunction)
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
    public void remove(T aggregate)
    {
        aggregateMap.remove( keyFunction.apply(aggregate)  );
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
    public <V> Optional<T> get(V primaryKey)
    {
        //noinspection SuspiciousMethodCalls
        return Optional.ofNullable(aggregateMap.get( primaryKey));
    }


    @Override
    public List<T> get()
    {
        return new ArrayList<>(aggregateMap.values());
    }
}
