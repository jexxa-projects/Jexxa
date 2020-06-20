package io.jexxa.utils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * This utility class allows to run methods which throw a checked exception inside a java stream 
 *
 * Example:
 * <pre>
 * {@code}
 * Integer[] values = {1,2,3};
 * Arrays.stream(values).
 *   forEach(
 *    exceptionLogger(value -&gt; Integer.divideUnsigned(value, 0))
 * );
 * {@code}
 * </pre>
 *
 *
 * @param <T> Lambda expression 
 * @param <E> Type of the exception
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;


    @SuppressWarnings("java:S1181") //Catch Throwable without warning
    static <T, E extends Exception> Consumer<T>
    exceptionLogger(ThrowingConsumer<T, E> throwingConsumer) {
        return i -> {
            try
            {
                throwingConsumer.accept(i);
            }
            catch (Exception e)
            {
                JexxaLogger.getLogger(throwingConsumer.getClass()).error(e.getMessage());
            }
        };
    }

    static <T> Consumer<T>
    exceptionCollector(ThrowingConsumer<T, Exception> throwingConsumer, Collection<Throwable> exceptions) {
        return i -> {
            try
            {
                throwingConsumer.accept(i);
            }
            catch (Exception e)
            {
                exceptions.add(e);
                JexxaLogger.getLogger(throwingConsumer.getClass()).error(e.getMessage());
            }
        };
    }

}

