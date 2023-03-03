package io.jexxa.drivenadapter.strategy.persistence.objectstore;

import java.util.List;

/**
 * Interface to search string based meta tags
 * <p>
 *  A note on NULL values:
 *  <ol>
 *  <li>Null values are supported </li>
 *  <li>When comparing values with a null value then a null value is treated always greater then a non-null value </li>
 *  <li>When getting objects in ascending or descending order, null values are always at the end of the list </li>
 *  </ol>
 *
 * @param <T> Type of the managed object
 * @param <S> Type of the element that is assigned to the meta tag
 */
public interface IStringQuery<T, S>
{
    /**
     * Returns all managed object whose element begins with given value
     * @param value describing the start of the searched value
     * @return list of managed objects that fulfill the condition
     */
    List<T> beginsWith(S value);

    /**
     * Returns all managed object whose element ends with given value
     * @param value describing the start of the searched value
     * @return list of managed objects that fulfill the condition
     */
    List<T> endsWith(S value);

    /**
     * Returns all managed object whose element includes given value
     *
     * @param value describing the searched included value
     * @return list of managed objects that fulfill the condition
     */
    List<T> includes(S value);

    /**
     * Returns all managed object whose element is equal to given value
     *
     * @param value describing the searched value
     * @return list of managed objects that fulfill the condition
     */
    List<T> isEqualTo(S value);

    /**
     * Returns all managed object whose element does not include given value
     *
     * @param value that must not be included in searched value
     * @return list of managed objects that fulfill the condition
     */
    List<T> notIncludes(S value);

    /**
     * Returns all managed object whose element is null
     *
     * @return list of managed objects that fulfill the condition
     */
    List<T> isNull();

    /**
     * Returns all managed object whose element is not null
     *
     * @return list of managed objects that fulfill the condition
     */
    List<T> isNotNull();

    /**
     * Sorts the entries by S in ascending order and returns the defined amount of elements
     * @param amount specifies the number of recent added aggregates that should be returned.
     * @return list of elements limited by the given amount.
     *         If less than requested aggregates are managed, all aggregates are returned.
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
     *         If less than requested aggregates are managed, all aggregates are returned.
     *         If amount is &lt; 0 then an empty list ist returned
     */
    List<T> getDescending(int amount);

    /**
     * Sorts the entries by S in descending order and returns elements
     * @return list of elements limited by the given amount.
     */
    List<T> getDescending();

}
