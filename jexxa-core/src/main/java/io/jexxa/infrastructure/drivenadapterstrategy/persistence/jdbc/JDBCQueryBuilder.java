package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JDBCQueryBuilder <T extends Enum<T>>
{
    private static final String SELECT = "select ";

    private String query = "";
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCQueryBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public enum SQLOperation
    {
        GREATER_THAN(">"),
        LESS_THAN("<"),
        EQUAL("=");

        private final String string;

        // constructor to set the string
        SQLOperation(String name){string = name;}

        // the toString just returns the given name
        @Override
        public String toString() {
            return string;
        }
    }

    public JDBCQueryBuilder<T> select(T element)
    {
        query += SELECT + element.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2 )
    {
        query += SELECT + element1.name() + ", " + element2.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2, T element3 )
    {
        query += SELECT + element1.name() + ", " + element2.name() + ", " + element3.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2, T element3, T element4 )
    {
        query += SELECT + element1.name() + ", " + element2.name() + ", " + element3.name() + ", " + element4.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> selectAll()
    {
        query += "select * ";
        return this;
    }


    public JDBCQueryBuilder<T> from(T element)
    {
        query += "from " + element.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> from(Class<?> clazz)
    {
        query += "from " + clazz.getSimpleName() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> where(T element)
    {
        query += "where " + element.name() + " ";
        return this;
    }

    public JDBCQueryBuilder<T> isEqual(Object value)
    {
        return is(SQLOperation.EQUAL, value);
    }

    public JDBCQueryBuilder<T> isLessThan(Object value)
    {
        return is(SQLOperation.LESS_THAN, value);
    }

    public JDBCQueryBuilder<T> isGreaterThan(Object value)
    {
        return is(SQLOperation.GREATER_THAN, value);
    }


    public String getQuery()
    {
        return query;
    }

    public JDBCQueryBuilder<T> is(SQLOperation operation, Object attribute)
    {
        query += operation.toString() + " ? ";
        arguments.add(attribute);
        return this;
    }

    public JDBCPreparedQuery create()
    {
        PreparedStatement preparedStatement;
        try
        {
            preparedStatement = jdbcConnection.get().prepareStatement(query);

            for (int i = 0; i < arguments.size(); ++i)
            {
                preparedStatement.setObject(i+1, arguments.get(i));
            }

        } catch (SQLException e)
        {
            throw new IllegalArgumentException("Invalid Query " + getQuery() + " " + e.getMessage(), e);
        }

        return new JDBCPreparedQuery(preparedStatement);
    }
}
