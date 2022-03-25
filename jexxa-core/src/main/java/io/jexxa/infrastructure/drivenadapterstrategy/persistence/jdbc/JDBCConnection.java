package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCCommandBuilder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCQueryBuilder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder;
import io.jexxa.utils.properties.JexxaJDBCProperties;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import io.jexxa.utils.properties.Secret;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class JDBCConnection implements AutoCloseable
{

    public static final int NO_TIMEOUT = 0;

    private Connection connection;
    private final Properties properties;

    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCConnection.class);

    public JDBCConnection(Properties properties)
    {
        validateProperties(properties);

        initDBDriver(properties);

        autocreateDatabase(properties);

        this.connection = initJDBCConnection(properties);
        this.properties = properties;
    }

    public final void autocreateDatabase(final Properties properties)
    {
        if (properties.containsKey(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE))
        {
            var splitURL = properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL).split("/");
            var dbName = splitURL[splitURL.length - 1].toLowerCase(Locale.ENGLISH); //last part of the URL is the name of the database (Note: Some DBs such as postgres require a name in lower case!)

            var creationProperties = new Properties();
            creationProperties.putAll(properties);

            var username = new Secret(creationProperties, JexxaJDBCProperties.JEXXA_JDBC_USERNAME, JexxaJDBCProperties.JEXXA_JDBC_FILE_USERNAME);
            var password = new Secret(creationProperties, JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, JexxaJDBCProperties.JEXXA_JDBC_FILE_PASSWORD);

            try (var setupConnection = DriverManager.
                    getConnection(
                            creationProperties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE),
                            username.getSecret(),
                            password.getSecret());
                 var statement = setupConnection.createStatement())
            {
                setupConnection.setAutoCommit(true);
                statement.execute(String.format("create DATABASE %s ", dbName));
                LOGGER.debug("Database {} successfully created ", dbName);
            }
            catch (SQLException e)
            {
                LOGGER.debug("Could not create database {} => Assume that database already exists", dbName);
            }
        }
    }

    @SuppressWarnings("java:S2139") // Here we log and rethrow an exception in order to document that we tried to handle a connection failure without success and must give up
    public final JDBCConnection validateConnection()
    {
        try
        {
            if (!isValid())
            {
                LOGGER.warn("JDBC connection for connection {} is invalid. ", properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL));
                LOGGER.warn("Try to reset JDBC connection for connection {}",  properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL));
                reset();
                LOGGER.warn("JDBC connection for connection {} successfully restarted.",  properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL));
            }
        } catch (RuntimeException e)
        {
            LOGGER.error("Could not reset JDBC connection for connection {}. Reason: {}", properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL), e.getMessage());
            throw e;
        }

        return this;
    }

    @SuppressWarnings("java:S1172")
    public <T extends Enum<T>> JDBCQueryBuilder<T> createQuery(Class<T> schema)
    {
        Objects.requireNonNull(schema);
        return new JDBCQueryBuilder<>(this::validateConnection);
    }

    public <T extends Enum<T>> JDBCCommandBuilder<T> createCommand(Class<T> schema)
    {
        Objects.requireNonNull(schema);
        return new JDBCCommandBuilder<>(this::validateConnection);
    }

    public <T extends Enum<T>> JDBCTableBuilder<T> createTableCommand(Class<T> schema)
    {
        Objects.requireNonNull(schema);
        return new JDBCTableBuilder<>(this::validateConnection);
    }

    /**
     * This method resets the internal JDBC connection in the following way:
     * <ol>
     *  <li>The existing JDBC connection is closed.</li>
     *  <li>A new JDBC connection is established based on the given properties in constructor.</li>
     *  <li>The new JDBC connection is validated using {@link Connection#isValid(int) }.</li>
     * </ol>
     * If any of these steps fails, an IllegalStateException is thrown including the error message from JDBC driver.
     */
    public void reset()
    {
        close();
        try
        {
            if (!getConnection().isValid(NO_TIMEOUT))
            {
                throw new IllegalStateException("JDBC Connection is invalid for connection " + properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL));
            }
        } catch (SQLException e)
        {
            throw new IllegalStateException("Could not reset JDCConnection. Reason: " + e.getMessage(), e);
        }
    }

    public boolean isValid()
    {
        return isValid(NO_TIMEOUT);
    }

    public boolean isValid(int timeout)
    {
        if ( connection == null )
        {
            return false;
        }
        try
        {
            return connection.isValid(timeout);
        } catch (SQLException e)
        {
            return false;
        }
    }

    /**
     * Creates a PreparedStatement
     *
     * @param sqlStatement describes the template of the command
     * @return PreparedStatement
     * @throws SQLException in case of an error
     */
    PreparedStatement prepareStatement(String sqlStatement) throws SQLException
    {
        return getConnection().prepareStatement(sqlStatement);
    }

    public void close()
    {
        Optional.ofNullable(connection)
                .ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));
        connection = null;
    }

    protected Connection getConnection()
    {
        if ( connection == null )
        {
            connection = initJDBCConnection(properties);
        }
        return connection;
    }

    private static Connection initJDBCConnection(Properties properties)
    {
        var username = new Secret(properties, JexxaJDBCProperties.JEXXA_JDBC_USERNAME, JexxaJDBCProperties.JEXXA_JDBC_FILE_USERNAME);
        var password = new Secret(properties, JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, JexxaJDBCProperties.JEXXA_JDBC_FILE_PASSWORD);

        try {
            var connection = DriverManager.getConnection(
                    properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL),
                    username.getSecret(),
                    password.getSecret()
            );

            connection.setAutoCommit(true);
            return connection;
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }


    private static void initDBDriver(Properties properties)
    {
        try
        {
            Class.forName(properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_DRIVER));
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Specified JDBC driver is not available: " + properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_DRIVER), e);
        }
    }

    private static void validateProperties(Properties properties)
    {
        Validate.isTrue(properties.containsKey(JexxaJDBCProperties.JEXXA_JDBC_URL), "Parameter " + JexxaJDBCProperties.JEXXA_JDBC_URL + " is missing");
        Validate.isTrue(properties.containsKey(JexxaJDBCProperties.JEXXA_JDBC_DRIVER), "Parameter " + JexxaJDBCProperties.JEXXA_JDBC_DRIVER + " is missing");
    }
}
