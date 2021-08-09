package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter;


@FunctionalInterface
public interface MetadataConverter
{
    <T, S, V> Converter<T, S, V> getValueConverter();
}