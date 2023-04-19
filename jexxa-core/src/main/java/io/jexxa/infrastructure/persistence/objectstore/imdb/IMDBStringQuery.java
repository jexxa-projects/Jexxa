package io.jexxa.infrastructure.persistence.objectstore.imdb;

import io.jexxa.infrastructure.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.persistence.objectstore.metadata.StringTag;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class IMDBStringQuery<T, K, S> implements IStringQuery<T, S>
{
    private final StringTag<T, S> stringTag;
    private final IMDBObjectStore<T, K, ?> objectStore;
    private Map<K, T> getOwnAggregateMap()
    {
        return objectStore.getAggregates();
    }

    IMDBStringQuery(IMDBObjectStore<T, K, ?> objectStore, StringTag<T, S> stringTag, Class<S> queryType)
    {
        this.objectStore = objectStore;
        this.stringTag = stringTag;
        Objects.requireNonNull( queryType );//Type required for java type inference
    }


    @Override
    public List<T> beginsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .filter( element -> stringTag.getFromAggregate(element).startsWith(stringTag.getFromValue(value)))
                .toList();
    }

    @Override
    public List<T> endsWith(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .filter( element -> stringTag.getFromAggregate(element).endsWith(stringTag.getFromValue(value)))
                .toList();
    }

    @Override
    public List<T> includes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .filter( element -> stringTag.getFromAggregate(element).contains(stringTag.getFromValue(value)))
                .toList();
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .filter( element -> stringTag.getFromAggregate(element).equals(stringTag.getFromValue(value)))
                .toList();
    }

    @Override
    public List<T> notIncludes(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .filter( element -> !stringTag.getFromAggregate(element).contains(stringTag.getFromValue(value)))
                .toList();
    }

    @Override
    public List<T> isNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) == null)
                .toList();
    }

    @Override
    public List<T> isNotNull()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter( element -> stringTag.getFromAggregate(element) != null)
                .toList();
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(stringTag::getFromAggregate))
                .limit(amount)
                .toList();
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(this::compareToAggregate)
                .toList();
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .limit(amount)
                .toList();
    }

    @Override
    public List<T> getDescending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> compareToAggregate(element2, element1))
                .toList();
    }

    protected int typeSpecificCompareTo(String value1, String value2)
    {
        return value1.compareTo(value2);
    }

    /**
     * Compares the value of the two aggregates which each other
     *
     * @param aggregate1 first aggregate
     * @param aggregate2 second aggregate
     * @return 0 If the value of aggregate1 is equal to value aggregate2 <br>
     *     -1 if value of aggregate1 &lt; value of aggregate2 <br>
     *     1 if value of aggregate1 &gt; value of aggregate2 <br>
     */
    @SuppressWarnings("DuplicatedCode")
    private int compareToAggregate(T aggregate1, T aggregate2)
    {
        Objects.requireNonNull(aggregate1);
        Objects.requireNonNull(aggregate2);

        var aggregateValue1 = stringTag.getFromAggregate(aggregate1);
        var aggregateValue2 = stringTag.getFromAggregate(aggregate2);

        if ( aggregateValue1 == null && aggregateValue2 == null)
        {
            return 0;
        } else if ( aggregateValue1 == null || aggregateValue2 == null)
        {
            return 1;
        }

        return typeSpecificCompareTo( aggregateValue1, aggregateValue2);
    }
}
