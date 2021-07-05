package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public
interface SearchStrategy
{
    <T,V> RangeComparator2<T,V> get();
}