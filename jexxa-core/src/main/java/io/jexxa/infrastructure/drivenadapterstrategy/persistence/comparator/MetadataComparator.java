package io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator;


@FunctionalInterface
public interface MetadataComparator
{
    <T,V> NumericComparator<T,V> getComparator();
}