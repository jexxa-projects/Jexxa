package io.jexxa.utils.properties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * This class can be used to read a secret from a Property. The secret can be either directly stored in Properties
 * with first key, or it is read from a file defined by the second key.
 *
 * The main responsibility of this class is to simplify the use of two different properties keys to get the information.
 */
public class Secret {
    private final Properties properties;
    private final String key;
    private final String fileKey;

    public Secret(Properties properties, String key, String fileKey)
    {
        this.properties = Objects.requireNonNull(properties);
        this.key = Objects.requireNonNull(key);
        this.fileKey = Objects.requireNonNull(fileKey);
    }

    /**
     * This method returns the secret in the following order:
     * 1. If the {@link #key} is defined in the internal {@link #properties} and it is not empty then this one is returned.
     * 2. If the {@link #fileKey} is defined in the internal {@link #properties} then this file is read and its content is returned.
     *
     * @return the secret defined either in property {@link #key} or stored in {@link #fileKey}. If both are not
     * available, or in case of any error an empty string is returned.
     */
    public String getSecret()
    {
        if (properties.getProperty(key) != null
                && !properties.getProperty(key).isEmpty())
        {
            return properties.getProperty(key);
        }

        try {
            if (properties.getProperty(fileKey) != null
                    && !properties.getProperty(fileKey).isEmpty())
            {
                return Files
                        .readAllLines(Path.of(properties.getProperty(fileKey)))
                        .get(0);
            }
        } catch (IOException e)
        {
            throw new IllegalArgumentException(e);
        }

        return "";
    }
}
