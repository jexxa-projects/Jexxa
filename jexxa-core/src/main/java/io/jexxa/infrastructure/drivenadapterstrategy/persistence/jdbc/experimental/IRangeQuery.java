package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;

public interface IRangeQuery<T, S >
{
    /**  get all values which fulfill:  startValue <= value
     */
    List<T> getFrom(S startValue);

    /** get all values which fulfill: startValue <= value <= endValue
     */
    List<T> getRange(S startValue, S endValue);

    /** get all values which fulfill:  value <= endValue
     */
    List<T> getUntil(S endValue);
}
