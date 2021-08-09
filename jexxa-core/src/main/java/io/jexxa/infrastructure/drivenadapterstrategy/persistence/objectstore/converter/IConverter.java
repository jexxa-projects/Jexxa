package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter;

@FunctionalInterface
public interface IConverter<T, R>
{
    R convert(T var1);
}
