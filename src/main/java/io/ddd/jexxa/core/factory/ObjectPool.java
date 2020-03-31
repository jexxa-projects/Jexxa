package io.ddd.jexxa.core.factory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ObjectPool
{
    private Set<Object> objectSet = new HashSet<>();

    public void add(Object object)
    {
      objectSet.add(object);
    }

    public void remove(Object object)
    {
        objectSet.remove(object);
    }

    public void remove(Class<?> clazz)
    {
        remove(
                objectSet.
                        stream().
                        filter( element -> element.getClass() == clazz).
                        findFirst().
                        orElse(null)
        );

    }

    public <T> Optional<T> getInstance(Class<T> clazz)
    {
        return objectSet.
                stream().
                filter( element -> clazz.isAssignableFrom(element.getClass())).
                findFirst().
                map(clazz::cast);
    }

}
