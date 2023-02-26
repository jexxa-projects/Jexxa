package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import io.jexxa.utils.properties.JexxaJDBCProperties;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static io.jexxa.adapterapi.JexxaContext.registerCleanupHandler;

public class JDBCConnectionPool implements AutoCloseable {

    private static final JDBCConnectionPool JDBC_CONNECTION_POOL = new JDBCConnectionPool();

    // Concurrent maps are used to handle optimized concurrent access to this class without synchronizing each method
    // This is possible because methods that are accessed in parallel, are synchronized by the maps (such as computeIfAbsent)
    private final Map<String, JDBCConnection> sharedConnectionMap = new ConcurrentHashMap<>();
    private final Map<Object, JDBCConnection> exclusiveConnectionMap = new ConcurrentHashMap<>();
    private final Map<Object, JDBCConnection.IsolationLevel> connectionConfiguration = new ConcurrentHashMap<>();

    public static JDBCConnection getConnection(Properties properties, Object managingObject)
    {
        var connectionName = properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL);

        if ( connectionName == null )
        {
            throw new IllegalArgumentException("Parameter " + JexxaJDBCProperties.JEXXA_JDBC_URL + " is missing");
        }

        if (JDBC_CONNECTION_POOL.requiresExclusiveConnection(managingObject))
        {
            return JDBC_CONNECTION_POOL.getExclusiveConnection(properties, managingObject);
        }
        return JDBC_CONNECTION_POOL.getSharedConnection(properties, connectionName);
    }


    public static void configureExclusiveConnection(Object managingObject, JDBCConnection.IsolationLevel isolationLevel)
    {
        JDBC_CONNECTION_POOL.connectionConfiguration.put(managingObject, isolationLevel);
    }


    private JDBCConnectionPool()
    {
        registerCleanupHandler(this::close);
    }



    private boolean requiresExclusiveConnection(Object managingObject)
    {
        return connectionConfiguration.containsKey(managingObject);
    }

    private JDBCConnection getSharedConnection(Properties properties, String connectionName)
    {
        return sharedConnectionMap
                .computeIfAbsent(connectionName, key -> new JDBCConnection(properties))
                .validateConnection();
    }

    private JDBCConnection getExclusiveConnection(Properties properties, Object managingObject)
    {
        return exclusiveConnectionMap
                .computeIfAbsent(managingObject, key -> {
                    var jdbcConnection = new JDBCConnection(properties);
                    jdbcConnection.setIsolationLevel(connectionConfiguration.get(managingObject));
                    return jdbcConnection;
                })
                .validateConnection();
    }

    @Override
    public void close() {
        sharedConnectionMap.forEach( ((s, jdbcConnection) -> jdbcConnection.close()));
        sharedConnectionMap.clear();

        exclusiveConnectionMap.forEach( ((s, jdbcConnection) -> jdbcConnection.close()));
        exclusiveConnectionMap.clear();
    }
}
