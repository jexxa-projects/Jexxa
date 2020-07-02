package io.jexxa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JexxaLogger
{
    public static Logger getLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }

    private JexxaLogger()
    {
        //Private constructor
    }
}
