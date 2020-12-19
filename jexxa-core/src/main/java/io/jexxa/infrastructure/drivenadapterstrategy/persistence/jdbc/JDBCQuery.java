package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class JDBCQuery
{
    private static final String INVALID_QUERY = "Invalid Query: ";

    private final Supplier<JDBCConnection> jdbcConnection;
    private String command;

    public JDBCQuery(Supplier<JDBCConnection> jdbcConnection)
    {
        this.jdbcConnection = jdbcConnection;
    }


    public JDBCQuery query(String command)
    {
        this.command = command;
        return this;
    }

    public Stream<String> asString()
    {
        Objects.requireNonNull(command);

        try (   var statement = jdbcConnection.get().createStatement();
                var resultSet = statement.executeQuery(command))
        {
            List<String> result = new ArrayList<>();
            while ( resultSet.next() )
            {
                result.add(resultSet.getString(1));
            }
            return result.stream();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException(INVALID_QUERY + command , e);
        }
    }

    Stream<BigDecimal> asNumeric()
    {
        Objects.requireNonNull(command);

        try ( var statement = jdbcConnection.get().createStatement();
              var resultSet = statement.executeQuery(command))
        {
            List<BigDecimal> result = new ArrayList<>();
            while ( resultSet.next() )
            {
                result.add(resultSet.getBigDecimal(1));
            }
            return result.stream();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException(INVALID_QUERY + command , e);
        }
    }

    public Stream<Instant> asTimestamp()
    {
        Objects.requireNonNull(command);

        try ( var statement = jdbcConnection.get().createStatement();
              var resultSet = statement.executeQuery(command))
        {
            List<Timestamp> result = new ArrayList<>();
            while ( resultSet.next() )
            {
                result.add(resultSet.getTimestamp(1));
            }
            return result.stream().map(Timestamp::toInstant);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException(INVALID_QUERY + command , e);
        }
    }


}
