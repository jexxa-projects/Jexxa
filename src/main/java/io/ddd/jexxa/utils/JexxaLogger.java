package io.ddd.jexxa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JexxaLogger
{
    public static Logger getLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }

    private JexxaLogger() {

    }
}
