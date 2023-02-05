package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import io.jexxa.utils.properties.JexxaJDBCProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JDBCConnectionPool implements AutoCloseable{

    private static final JDBCConnectionPool JDBC_CONNECTION_POOL = new JDBCConnectionPool();

    private final Map<String, JDBCConnection> connectionMap = new HashMap<>();
    static public JDBCConnection getConnection(Properties properties)
    {
       return JDBC_CONNECTION_POOL.getInternalConnection(properties);
    }

    private JDBCConnection getInternalConnection(Properties properties)
    {
        var connectionName = properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL);

        if ( connectionName == null )
        {
            throw new IllegalArgumentException("Parameter " + JexxaJDBCProperties.JEXXA_JDBC_URL + " is missing");
        }

        if (!connectionMap.containsKey(connectionName))
        {
            var newConnection = new JDBCConnection(properties);
            connectionMap.put(properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL), newConnection);
        }

        return connectionMap.get(connectionName).validateConnection();
    }

    @Override
    public void close() {
        connectionMap.forEach( ((s, jdbcConnection) -> jdbcConnection.close()));
        connectionMap.clear();
    }
}
