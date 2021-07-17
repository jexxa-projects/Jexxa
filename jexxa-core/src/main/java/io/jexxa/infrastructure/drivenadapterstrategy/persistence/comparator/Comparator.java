package io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator;

public interface Comparator<T, S, V>
{
    /**
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param aggregate which provides the aggregate including the value that should be converted
     * @return Number representing the value
     */
    public V convertAggregate(T aggregate);

    /**
     * This method converts the value of type {@link S} stored in the aggregate to a Number by using the defined converter function
     * @param value which provides the value that should be converted
     * @return Number representing the value
     */
    public V convertValue(S value);

    /**
     * Compares the value of the aggregate with given value
     *
     * @param aggregate first aggregate
     * @param value that should be compared
     * @return 0 If the value aggregate is equal to given value <br>
     *     -1 if value of aggregate &lt; given value <br>
     *     1 if value of aggregate &gt; value <br>
     */
    public abstract int compareToValue(T aggregate, S value);

    /**
     * Compares the value of the two aggregates which each other
     *
     * @param aggregate1 first aggregate
     * @param aggregate2 second aggregate
     * @return 0 If the value of aggregate1 is equal to value aggregate2 <br>
     *     -1 if value of aggregate1 &lt; value of aggregate2 <br>
     *     1 if value of aggregate1 &gt; value of aggregate2 <br>
     */
    public abstract int compareToAggregate(T aggregate1, T aggregate2);
}
