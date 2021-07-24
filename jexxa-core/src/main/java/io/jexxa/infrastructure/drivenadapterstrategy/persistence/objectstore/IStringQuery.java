package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import java.util.List;

/**
 *
 * @param <T> Type of the managed object
 * @param <S> Type of the metadata that is used to find the objects
 */
public interface IStringQuery<T, S >
{
    List<T> beginsWith(S value);
    List<T> endsWith(S value);
    List<T> includes(S value);
    List<T> isEqualTo(S value);
    List<T> notIncludes(S value);
    List<T> isNull();
    List<T> isNotNull();


    /**
     * Sorts the entries by S in ascending order and returns the defined amount of elements
     * @param amount specifies the number of recent added aggregates that should be returned.
     * @return list of elements limited by the given amount.
     *         If less then requested aggregates are managed, all aggregates are returned.
     *         If amount is &lt; 0 then an empty list ist returned
     */
    List<T> getAscending(int amount);

    /**
     * Sorts the entries by S in ascending order and returns elements
     * @return list of elements limited by the given amount.
     */
    List<T> getAscending();

    /**
     * Sorts the entries by S in descending order and returns the defined amount of elements
     * @param amount specifies the number of recent added aggregates that should be returned.
     * @return list of elements limited by the given amount.
     *         If less then requested aggregates are managed, all aggregates are returned.
     *         If amount is &lt; 0 then an empty list ist returned
     */
    List<T> getDescending(int amount);

    /**
     * Sorts the entries by S in descending order and returns elements
     * @return list of elements limited by the given amount.
     */
    List<T> getDescending();

}
