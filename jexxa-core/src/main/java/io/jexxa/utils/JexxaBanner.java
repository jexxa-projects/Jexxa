package io.jexxa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public final class JexxaBanner
{
    private static final JexxaBanner JEXXA_BANNER  = new JexxaBanner();
    private final List<Consumer<Properties>> banner= new ArrayList<>();



    public static void addBanner(Consumer<Properties> consumer)
    {
        JEXXA_BANNER.banner.add(consumer);
    }

    public static void show(Properties properties)
    {
        JEXXA_BANNER.banner.forEach( element -> element.accept(properties));
    }

    private JexxaBanner()
    {
        // private constructor
    }
}
