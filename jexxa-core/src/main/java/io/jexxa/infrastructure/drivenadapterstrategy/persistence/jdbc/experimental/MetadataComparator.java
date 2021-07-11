package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public interface MetadataComparator
{
    <T,V> Comparator<T,V> getComparator();
}