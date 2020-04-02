package io.ddd.jexxa.utils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * This utility class allows to run methods which throw a checked exception inside a java stream 
 *
 * Example:
 * <pre>
 * <code>
 * Integer[] values = {1,2,3};
 * Arrays.stream(values).
 *        forEach(
 *          exceptionLogger(value -> { var result = value / 0;}) // Logs each exception
 *        );
 *
 * </code>
 * </pre>
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws E;


    @SuppressWarnings("java:S1181") //In order to catch Throwable without warning  
    static <T, E extends Throwable> Consumer<T>
    exceptionLogger(ThrowingConsumer<T, E> throwingConsumer) {
        return i -> {
            try
            {
                throwingConsumer.accept(i);
            }
            catch (Throwable e)
            {
                JexxaLogger.getLogger(throwingConsumer.getClass()).error(e.getMessage());
            }
        };
    }

    static <T> Consumer<T>
    exceptionCollector(ThrowingConsumer<T, Throwable> throwingConsumer, Collection<Throwable> exceptions) {
        return i -> {
            try
            {
                throwingConsumer.accept(i);
            }
            catch (Throwable e)
            {
                exceptions.add(e);
                JexxaLogger.getLogger(throwingConsumer.getClass()).error(e.getMessage());
            }
        };
    }

}

