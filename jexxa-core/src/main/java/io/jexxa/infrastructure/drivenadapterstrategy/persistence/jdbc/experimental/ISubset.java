package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;

public interface ISubset<T, S >
{
    /**  get all values which fulfill: {@code startValue <= value}
     */
    List<T> getFrom(S startValue);

    /** get all values which fulfill: {@code startValue <= value <= endValue}
     */
    List<T> getRange(S startValue, S endValue);

    /** get all values which fulfill:  {@code value <= endValue}
     */
    List<T> getUntil(S endValue);

    /**
     * Sorts the entries by S in ascending order and returns the defined amount of elements
     * @param amount specifies the number of recent added aggregates that should be returned.
     * @return list of elements limited by the given amount.
     *         If less then requested aggregates are managed, all aggregates are returned.
     *         If amount is &lt; 0 then an empty list ist returned
     */
    List<T> getAscending(int amount);

    /**
     * Sorts the entries by S in descending order and returns the defined amount of elements
     * @param amount specifies the number of recent added aggregates that should be returned.
     * @return list of elements limited by the given amount.
     *         If less then requested aggregates are managed, all aggregates are returned.
     *         If amount is &lt; 0 then an empty list ist returned
     */
    List<T> getDescending(int amount);

    /**
     * Returns all elements equal to S
     * @param value specifies comparison value
     * @return list of elements that are equal to value
     */
    List<T> get(S value);
}
