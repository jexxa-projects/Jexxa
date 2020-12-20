package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;


@FunctionalInterface
public
interface Strategy  {
    <T,V> Comparator<T,V> getStrategy();
}