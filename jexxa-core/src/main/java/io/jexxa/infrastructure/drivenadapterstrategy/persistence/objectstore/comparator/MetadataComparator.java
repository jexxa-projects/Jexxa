package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator;


@FunctionalInterface
public interface MetadataComparator
{
    <T, S, V> Comparator<T, S, V> getComparator();
}