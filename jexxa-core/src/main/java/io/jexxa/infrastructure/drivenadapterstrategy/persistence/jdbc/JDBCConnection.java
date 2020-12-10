package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class JDBCConnection implements AutoCloseable
{
    public static final String JDBC_URL = "io.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.jexxa.jdbc.driver";
    public static final String JDBC_AUTOCREATE_DATABASE = "io.jexxa.jdbc.autocreate.database";

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

    protected Connection getConnection()
    {
        if ( connection == null )
        {
            connection = initJDBCConnection(properties);
        }
        return connection;
    }

    private static Connection initJDBCConnection(final Properties properties)
    {

        try {
            var connection = DriverManager.getConnection(
                    properties.getProperty(JDBC_URL),
                    properties.getProperty(JDBC_USERNAME),
                    properties.getProperty(JDBC_PASSWORD)
            );

            connection.setAutoCommit(true);
            return connection;
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public void autocreateDatabase(final Properties properties)
    {
        if (properties.containsKey(JDBC_AUTOCREATE_DATABASE))
        {
            var splitURL = properties.getProperty(JDBC_URL).split("/");
            var dbName = splitURL[splitURL.length - 1].toLowerCase(Locale.ENGLISH); //last part of the URL is the name of the database (Note: Some DBs such as postgres require a name in lower case!)

            Properties creationProperties = new Properties();
            creationProperties.putAll(properties);

            try (var setupConnection = DriverManager.
                    getConnection(
                            creationProperties.getProperty(JDBC_AUTOCREATE_DATABASE),
                            creationProperties.getProperty(JDBC_USERNAME),
                            creationProperties.getProperty(JDBC_PASSWORD));
                 Statement statement = setupConnection.createStatement())
            {
                setupConnection.setAutoCommit(true);
                statement.execute(String.format("create DATABASE %s ", dbName));
                LOGGER.info("Database {} successfully created ", dbName);
            }
            catch (SQLException e)
            {
                LOGGER.warn("Could not create database {} => Assume that database already exists", dbName);
            }
        }
    }

    public void reset()
    {
        close();
        connection = null;
    }

    public boolean isValid()
    {
        return isValid(0);
    }

    public boolean isValid(int timeout)
    {
        try
        {
            if ( connection == null)
            {
                return false;
            }

            return connection.isValid(timeout);
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    public Statement createStatement() throws SQLException
    {
        return getConnection().createStatement();
    }

    public PreparedStatement prepareStatement(String sqlStatement) throws SQLException
    {
        return getConnection().prepareStatement(sqlStatement);
    }

    private static void validateProperties(Properties properties)
    {
        Validate.isTrue(properties.containsKey(JDBC_URL), "Parameter " + JDBC_URL + " is missing");
        Validate.isTrue(properties.containsKey(JDBC_DRIVER), "Parameter " + JDBC_DRIVER + " is missing");
    }

    public void close()
    {
        Optional.ofNullable(connection)
                .ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));
    }

    private static void initDBDriver(Properties properties)
    {
        try
        {
            Class.forName(properties.getProperty(JDBC_DRIVER));
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Specified JDBC driver is not available: " + properties.getProperty(JDBC_DRIVER), e);
        }

    }

}
