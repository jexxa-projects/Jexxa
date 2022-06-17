package io.jexxa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public final class JexxaBanner
{
    private static final JexxaBanner JEXXA_BANNER  = new JexxaBanner();
    private final List<Consumer<Properties>> configBanner = new ArrayList<>();
    private final List<Consumer<Properties>> accessBanner = new ArrayList<>();



    public static void addConfigBanner(Consumer<Properties> consumer)
    {
        JEXXA_BANNER.configBanner.add(consumer);
    }

    public static void addAccessBanner(Consumer<Properties> consumer)
    {
        JEXXA_BANNER.accessBanner.add(consumer);
    }

    public static void show(Properties properties)
    {
        JexxaLogger.getLogger(JexxaBanner.class).info("Config Information: ");
        JEXXA_BANNER.configBanner.forEach(element -> element.accept(properties));

        JexxaLogger.getLogger(JexxaBanner.class).info("");
        JexxaLogger.getLogger(JexxaBanner.class).info("Access Information: ");
        JEXXA_BANNER.accessBanner.forEach(element -> element.accept(properties));
    }

    private JexxaBanner()
    {
        // private constructor
    }
}
