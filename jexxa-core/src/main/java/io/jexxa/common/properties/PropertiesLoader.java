package io.jexxa.common.properties;

import io.jexxa.common.JexxaCoreProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.jexxa.common.JexxaCoreProperties.JEXXA_APPLICATION_PROPERTIES;
import static io.jexxa.common.JexxaCoreProperties.JEXXA_USER_TIMEZONE;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;

public class PropertiesLoader {
    private final Class<?> context;
    final Properties properties = new Properties();
    private final List<String> propertiesFiles = new ArrayList<>();

    public PropertiesLoader(Class<?> context) {
        this.context = Objects.requireNonNull(context);
    }

    public Properties createJexxaProperties(Properties applicationProperties) {
        properties.clear();
        propertiesFiles.clear();

        // Handle properties in following forder:
        // 0. Add default JEXXA_CONTEXT_MAIN
        this.properties.put(JexxaCoreProperties.JEXXA_CONTEXT_NAME, context.getSimpleName());

        // 1. Load properties from application.properties because they have the lowest priority
        loadJexxaApplicationProperties(this.properties);
        // 2. Use System properties because they have mid-priority
        this.properties.putAll(System.getProperties());  //add/overwrite system properties
        // 3. Use given properties because they have the highest priority
        this.properties.putAll(applicationProperties);  //add/overwrite given properties
        // 4. import properties that are defined by '"io.jexxa.config.import"'
        if (this.properties.containsKey(JexxaCoreProperties.JEXXA_CONFIG_IMPORT)) {
            importProperties(this.properties.getProperty(JexxaCoreProperties.JEXXA_CONFIG_IMPORT));
        }

        //5. set system properties
        setSystemProperties(properties);

        return removeEmptyValues(properties);
    }

    private void setSystemProperties(Properties properties) {
        if (properties.containsKey(JEXXA_USER_TIMEZONE))
        {
            System.getProperties().setProperty("user.timezone", properties.getProperty(JEXXA_USER_TIMEZONE));
        }
    }

    public List<String> getPropertiesFiles() {
        return propertiesFiles;
    }

    private void loadJexxaApplicationProperties(Properties properties) {
        try ( InputStream inputStream = PropertiesLoader.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES) )
        {
            if (inputStream != null) {
                properties.load(inputStream);
                propertiesFiles.add(JEXXA_APPLICATION_PROPERTIES);
            } else {
                getLogger(PropertiesLoader.class).warn("Default properties file {} not available", JEXXA_APPLICATION_PROPERTIES);
            }
        } catch ( IOException e ) {
            getLogger(PropertiesLoader.class).warn("Default properties file {} not available", JEXXA_APPLICATION_PROPERTIES);
        }
    }

    private Properties removeEmptyValues(Properties properties) {
        var filteredMap = properties.entrySet()
                .stream()
                .filter(entry -> (entry.getValue() != null && !entry.getValue().toString().isEmpty()))
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));

        properties.clear();
        properties.putAll(filteredMap);
        return properties;
    }

    public void importProperties(String resource) {
        //1. try to import properties as Resource from inside the jar
        try (InputStream resourceStream = PropertiesLoader.class.getResourceAsStream(resource)) {
            if (resourceStream != null) {
                properties.load(resourceStream);
                propertiesFiles.add(resource);
            } else {
                throw new FileNotFoundException(resource);
            }
        } catch (IOException e) {
            //2. try to import properties from outside the jar
            try (FileInputStream file = new FileInputStream(resource)) {
                properties.load(file);
                propertiesFiles.add(resource);
            } catch (IOException f) {
                throw new IllegalArgumentException("Properties file " + resource + " not available. Please check the filename!", f);
            }
        }
    }

}
