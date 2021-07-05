package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public
interface ComparatorStrategy
{
    <T,V> Comparator<T,V> getComparator();
}