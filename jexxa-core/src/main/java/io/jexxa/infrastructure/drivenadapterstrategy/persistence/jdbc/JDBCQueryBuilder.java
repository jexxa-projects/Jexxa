package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SELECT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCQueryBuilder <T extends Enum<T>>
{
    private final StringBuilder sqlQueryBuilder = new StringBuilder();
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCQueryBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCQueryBuilder<T> select(T element)
    {
        sqlQueryBuilder
                .append(SELECT)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2 )
    {
        sqlQueryBuilder
                .append(SELECT)
                .append(element1.name())
                .append(COMMA)
                .append(element2.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2, T element3 )
    {
        sqlQueryBuilder
                .append(SELECT)
                .append(element1.name())
                .append(COMMA)
                .append(element2.name())
                .append(COMMA)
                .append(element3.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> select(T element1, T element2, T element3, T element4 )
    {
        sqlQueryBuilder
                .append(SELECT)
                .append(element1.name())
                .append(COMMA)
                .append(element2.name())
                .append(COMMA)
                .append(element3.name())
                .append(COMMA)
                .append(element4.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> selectAll()
    {
        sqlQueryBuilder
                .append(SELECT)
                .append(" * ");
        return this;
    }


    public JDBCQueryBuilder<T> from(T element)
    {
        sqlQueryBuilder
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> from(Class<?> clazz)
    {
        sqlQueryBuilder
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> where(T element)
    {
        sqlQueryBuilder
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> isEqual(Object value)
    {
        return is(EQUAL, value);
    }

    public JDBCQueryBuilder<T> isLessThan(Object value)
    {
        return is(LESS_THAN, value);
    }

    public JDBCQueryBuilder<T> isGreaterThan(Object value)
    {
        return is(GREATER_THAN, value);
    }


    public String getQuery()
    {
        return sqlQueryBuilder.toString();
    }

    public JDBCQueryBuilder<T> is(SQLSyntax.SQLOperation operation, Object attribute)
    {
        sqlQueryBuilder
                .append(operation.toString())
                .append(ARGUMENT_PLACEHOLDER);

        arguments.add(attribute);

        return this;
    }

    public JDBCQuery create()
    {
        return new JDBCQuery(jdbcConnection, sqlQueryBuilder.toString(), arguments);
    }
}
