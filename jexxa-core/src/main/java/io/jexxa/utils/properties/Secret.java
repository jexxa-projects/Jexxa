package io.jexxa.utils.properties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Secret {
    private final Properties properties;
    private final String key;
    private final String fileKey;

    public Secret(Properties properties, String key, String fileKey)
    {
        this.properties = properties;
        this.key = key;
        this.fileKey = fileKey;
    }

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
