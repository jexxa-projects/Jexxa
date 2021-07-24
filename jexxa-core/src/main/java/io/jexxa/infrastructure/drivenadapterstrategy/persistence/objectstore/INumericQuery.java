package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import java.util.List;

/**
 *
 * TODO: Document comparison with null
 * TODO: Document Ascending/descending with null
 *
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

    /**  get all values which fulfill: {@code value < returnedValue}. The returned list is in ascending
     * order by numeric representation of used value.
     */
    List<T> isGreaterThan(S value);

    /** get all values which fulfill:  {@code value <= endValue}. The returned list is in ascending
     * order by numeric representation of used value.
     */
    List<T> isLessOrEqualThan(S endValue);

    /** get all values which fulfill:  {@code value < endValue}. The returned list is in ascending
     * order by numeric representation of used value.
     */
    List<T> isLessThan(S endValue);

    /**
     * Returns all elements equal to S
     * @param value specifies comparison value
     * @return list of elements that are equal to value
     */
    List<T> isEqualTo(S value);

    /**
     * Returns all elements not equal to S. The returned list is in ascending
     * order by numeric representation of used value.
     *
     * @param value specifies comparison value
     * @return an ordered list of elements that are equal to value
     */
    List<T> isNotEqualTo(S value);

    List<T> isNull();

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
     * @return an ordered list of elements that are in a open range.
     */
    List<T> getRange(S startValue, S endValue);

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
