package io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.imdb;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.utils.json.JSONManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public class IMDBRepository<T, K>  implements IRepository<T, K>
{
    // Each IMDB repository is represented by a map for a specific type.
    private static final Map< Class<?>, Map<?,?>> REPOSITORY_MAP = new ConcurrentHashMap<>();
    private static final Map< Class<?>, IMDBRepository<?,?> > IMDB_REPOSITORY_MAP = new ConcurrentHashMap<>();


    private Map<K, T> aggregateMap;
    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;

    @SuppressWarnings("java:S1172")
    public IMDBRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties ignoredProperties)
    {
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.keyFunction = Objects.requireNonNull(keyFunction);
        IMDB_REPOSITORY_MAP.put(aggregateClazz, this);

        aggregateMap = getOwnAggregateMap();
    }

    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);
        if (! getOwnAggregateMap().containsKey( keyFunction.apply(aggregate)))
        {
            var keyAsString = keyFunction.apply(aggregate).getClass().getSimpleName()
                    + JSONManager.getJSONConverter().toJson(keyFunction.apply(aggregate));
            throw new IllegalArgumentException(IMDBRepository.class.getSimpleName()
                    + ": An object with given key "
                    + keyAsString
                    + " does not exists");
        }
    }

    @Override
    public void remove(K key)
    {
        if ( getOwnAggregateMap().remove( key ) == null)
        {
            var keyAsString = key.getClass().getSimpleName() + JSONManager.getJSONConverter().toJson(key);
            throw new IllegalArgumentException(IMDBRepository.class.getSimpleName()
                    + ": An object with given "
                    + keyAsString
                    + "key does not exists");
        }
    }

    @Override
    public void removeAll()
    {
        getOwnAggregateMap().clear();
    }

    @Override
    public void add(T aggregate)
    {
        if (getOwnAggregateMap().containsKey( keyFunction.apply(aggregate)))
        {
            var keyAsString = keyFunction.apply(aggregate).getClass().getSimpleName()
                    + JSONManager.getJSONConverter().toJson(keyFunction.apply(aggregate));
            throw new IllegalArgumentException(IMDBRepository.class.getSimpleName()
                    + ": An object with given key "
                    + keyAsString
                    + " already exists");
        }
        getOwnAggregateMap().put(keyFunction.apply(aggregate), aggregate);
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        return Optional.ofNullable(getOwnAggregateMap().get( primaryKey));
    }


    @Override
    public List<T> get()
    {
        return new ArrayList<>(getOwnAggregateMap().values());
    }

    protected final Map<K, T> getOwnAggregateMap()
    {
        if ( aggregateMap == null ) {
            aggregateMap = getAggregateMap(aggregateClazz);
        }

        return aggregateMap;
    }

    /**
     * This method resets all IMDBRepositories instance within an application and removes all stored objects!
     * <p>
     * So this method should only be used when writing tests to ensure a clean data setup!
     */
    public static synchronized void clear()
    {
        IMDB_REPOSITORY_MAP.forEach( (aggregateType, repository) -> repository.removeAll() );
        IMDB_REPOSITORY_MAP.forEach( (aggregateType, repository) -> repository.resetIMDBInstance() );
        REPOSITORY_MAP.forEach( (aggregateType, imdbMap) -> imdbMap.clear() );
        REPOSITORY_MAP.clear();
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T, K> Map<T, K> getAggregateMap(Class<?> aggregateClazz)
    {
        return (Map<T, K>)REPOSITORY_MAP.computeIfAbsent(aggregateClazz, element -> new ConcurrentHashMap<T,K>());
    }

    protected void resetIMDBInstance()
    {
        aggregateMap = null;
    }

}
