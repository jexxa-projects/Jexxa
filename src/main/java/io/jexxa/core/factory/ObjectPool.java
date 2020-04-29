package io.jexxa.core.factory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ObjectPool
{
    private final Set<Object> objectSet = new HashSet<>();

    public void add(Object object)
    {
      objectSet.add(object);
    }
    
    public <T> Optional<T> getInstance(Class<T> clazz)
    {
        return objectSet.stream()
                .filter( element -> clazz.isAssignableFrom(element.getClass()))
                .findFirst()
                .map(clazz::cast);
    }

}
