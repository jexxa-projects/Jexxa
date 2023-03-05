package io.jexxa.infrastructure.persistence.objectstore;

import java.util.List;

/**
 * Interface to search numeric based meta tags
 * <p>
 * A note on NULL values:
 * <ol>
 * <li>Null values are supported </li>
 * <li>When comparing values with a null value then a null value is treated always greater then a non-null value </li>
 * <li>When getting objects in ascending or descending order, null values are always at the end of the list </li>
 * </ol>
 * @param <T> Type of the managed object
 * @param <S> Type of the metadata that is used to find the objects
 */
public interface INumericQuery<T, S >
{
    /**
     * Get all values which fulfill: {@code value <= metadata of the object}. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @param value concrete value that is used for comparison
     * @return an ordered list of all objects that fulfill the condition {@code value <= metadata of the object}.
     */
    List<T> isGreaterOrEqualThan(S value);

    /**
     * Get all values which fulfill: {@code value < returnedValue}. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @param value value to be compared to
     * @return list of objects fulfilling the condition
     */
    List<T> isGreaterThan(S value);

    /**
     * get all values which fulfill:  {@code value <= endValue}. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @param endValue value to be compared to
     * @return list of objects fulfilling the condition
     */
    List<T> isLessOrEqualThan(S endValue);

    /**
     * get all values which fulfill:  {@code value < endValue}. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @param endValue value to be compared to
     * @return list of objects fulfilling the condition
     */
    List<T> isLessThan(S endValue);

    /**
     * Returns all elements equal to S
     * @pre value must not be null
     * @param value specifies comparison value
     * @return list of elements that are equal to value
     */
    List<T> isEqualTo(S value);

    /**
     * Returns all elements not equal to S. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @pre value must not be null
     * @param value specifies comparison value
     * @return an ordered list of elements that are equal to value
     */
    List<T> isNotEqualTo(S value);

    /**
     * Returns all elements in which is S is null. The returned list is not ordered in any way.
     *
     * @return A list of elements that are equal to value
     */
    List<T> isNull();

    /**
     * Returns all elements in which is S is not null. The returned list is not in ascending order
     *
     * @return A list of elements that are equal to value
     */
    List<T> isNotNull();

    /**
     *  Get all values which fulfill: {@code startValue <= value <= endValue}. The returned list is in ascending
     *  order by numeric representation of used value.
     *
     * @param startValue defines the start value of the range
     * @param endValue defines the end value of the range
     * @return an ordered list of elements that are in a closed range.
     */
    List<T> getRangeClosed(S startValue, S endValue);

    /**
     *  Get all values which fulfill: {@code startValue <= value < endValue}. The returned list is in ascending
     *  order by numeric representation of used value.
     *
     * @param startValue defines the start value of the range
     * @param endValue defines the end value of the range
     * @return an ordered list of elements that are in an open range.
     */
    List<T> getRange(S startValue, S endValue);

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
