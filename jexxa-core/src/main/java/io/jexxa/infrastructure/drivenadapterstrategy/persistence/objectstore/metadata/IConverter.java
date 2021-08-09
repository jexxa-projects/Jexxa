package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata;

@FunctionalInterface
public interface IConverter<T, R>
{
    R convert(T var1);
}
