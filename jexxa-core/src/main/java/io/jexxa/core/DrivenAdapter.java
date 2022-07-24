package io.jexxa.core;

import io.jexxa.core.convention.AdapterConvention;

import java.util.Objects;
import java.util.function.Consumer;

public class DrivenAdapter<T> {
    private final JexxaMain jexxaMain;
    private final Class<T> drivenAdapterClass;

    DrivenAdapter(Class<T> drivenAdapterClass, JexxaMain jexxaMain) {
        AdapterConvention.validate(drivenAdapterClass);

        this.drivenAdapterClass = Objects.requireNonNull(drivenAdapterClass);
        this.jexxaMain = Objects.requireNonNull(jexxaMain);
    }

    public  JexxaMain to(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        jexxaMain.attachTo(drivenAdapterClass, consumer);

        return jexxaMain;
    }
}
