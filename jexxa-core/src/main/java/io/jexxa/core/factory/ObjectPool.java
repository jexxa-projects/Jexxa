package io.jexxa.core.factory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class ObjectPool
{
    private final Set<Object> objectSet = new HashSet<>();

    void add(Object object)
    {
      objectSet.add(object);
    }
    
    <T> Optional<T> getInstance(Class<T> clazz)
    {
        return objectSet.stream()
                .filter( element -> clazz.isAssignableFrom(element.getClass()))
                .findFirst()
                .map(clazz::cast);
    }

    void clear()
    {
        objectSet.clear();
    }

}
