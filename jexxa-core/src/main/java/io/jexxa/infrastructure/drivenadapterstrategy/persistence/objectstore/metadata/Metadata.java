package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata;


@FunctionalInterface
public interface Metadata
{
    <T, S, V> MetaTag<T, S, V> getMetaTag();
}