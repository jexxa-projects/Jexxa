package io.jexxa.utils.properties;

import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.jexxa.utils.properties.JexxaCoreProperties.JEXXA_APPLICATION_PROPERTIES;

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

        return removeEmptyValues(properties);
    }

    public List<String> getPropertiesFiles() {
        return propertiesFiles;
    }

    private void loadJexxaApplicationProperties(Properties properties) {
        Optional.ofNullable(PropertiesLoader.class.getResourceAsStream(JEXXA_APPLICATION_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load),
                        () -> JexxaLogger.getLogger(PropertiesLoader.class).warn("NO PROPERTIES FILE FOUND {}", JEXXA_APPLICATION_PROPERTIES)
                );

        propertiesFiles.add(JEXXA_APPLICATION_PROPERTIES);
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
            } else {
                throw new FileNotFoundException(resource);
            }
        } catch (IOException e) {
            //2. try to import properties from outside the jar
            try (FileInputStream file = new FileInputStream(resource)) {
                properties.load(file);
            } catch (IOException f) {
                throw new IllegalArgumentException("Properties file " + resource + " not available. Please check the filename!", f);
            }
        }
        propertiesFiles.add(resource);
    }

}
