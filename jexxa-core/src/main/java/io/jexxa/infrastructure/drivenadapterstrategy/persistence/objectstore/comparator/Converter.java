package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator;

@FunctionalInterface
public interface Converter<T, R>
{
    R convert(T var1);
}
