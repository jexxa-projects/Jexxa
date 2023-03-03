package io.jexxa.api.wrapper.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SLF4jLogger
{
    public static Logger getLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }

    private SLF4jLogger()
    {
        //Private constructor
    }
}