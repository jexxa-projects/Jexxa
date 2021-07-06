package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public
interface ComparatorSchema
{
    <T,V> Comparator<T,V> getComparator();
}