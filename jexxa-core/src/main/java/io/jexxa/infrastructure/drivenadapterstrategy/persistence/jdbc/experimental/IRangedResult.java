package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;

public interface IRangedResult <T, S >
{
    /*  startValue startValue <= value
     * */
    List<T> getFrom(S startValue);

    /*  startValue <= value <= endValue
     * */
    List<T> getRange(S startValue, S endValue);

    /*  value <= endValue
     * */
    List<T> getUntil(S endValue);
}
