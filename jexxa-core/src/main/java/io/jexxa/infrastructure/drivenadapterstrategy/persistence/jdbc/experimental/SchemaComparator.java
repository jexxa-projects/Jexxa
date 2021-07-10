package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public interface SchemaComparator
{
    <T,V> Comparator<T,V> getComparator();
}