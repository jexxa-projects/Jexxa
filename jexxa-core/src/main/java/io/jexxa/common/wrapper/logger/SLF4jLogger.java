package io.jexxa.common.wrapper.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SLF4jLogger
{
    public static Logger getSLF4JLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }
    public static Logger getLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }

    private SLF4jLogger()
    {
        //Private constructor
    }
}