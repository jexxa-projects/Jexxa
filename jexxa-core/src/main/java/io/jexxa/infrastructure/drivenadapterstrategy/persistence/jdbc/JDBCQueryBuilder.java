package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.AND;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.OR;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SELECT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LIKE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    @SafeVarargs
    public final JDBCQueryBuilder<T> select(T element, T... elements)
    {
        select(element);

        Stream.of( elements )
                .forEach( entry -> sqlQueryBuilder
                        .append(COMMA)
                        .append(entry.name())
                        .append(BLANK)
                );

        return this;
    }

    public JDBCQueryBuilder<T> selectAll()
    {
        sqlQueryBuilder
                .append(SELECT)
                .append("* ");
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

    public JDBCCondition<T> where(T element)
    {
        sqlQueryBuilder
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T> and(T element)
    {
        sqlQueryBuilder
                .append(AND)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T> or(T element)
    {
        sqlQueryBuilder
                .append(OR)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public String getQuery()
    {
        return sqlQueryBuilder.toString();
    }

    public JDBCQuery create()
    {
        return new JDBCQuery(jdbcConnection, sqlQueryBuilder.toString(), arguments);
    }

    StringBuilder getSqlQueryBuilder()
    {
        return sqlQueryBuilder;
    }

    void addArgument(Object argument)
    {
        arguments.add(argument);
    }

    public static class JDBCCondition<T extends Enum<T>>
    {
        private final JDBCQueryBuilder<T> queryBuilder;

        JDBCCondition( JDBCQueryBuilder<T> queryBuilder)
        {
            this.queryBuilder = queryBuilder;
        }

        public JDBCQueryBuilder<T> isEqual(Object value)
        {
            return is(EQUAL, value);
        }

        public JDBCQueryBuilder<T> isLessThan(Object value)
        {
            return is(LESS_THAN, value);
        }

        public JDBCQueryBuilder<T> isLessOrEqual(Object value)
        {
            return is(LESS_THAN_OR_EQUAL, value);
        }

        public JDBCQueryBuilder<T> isGreaterThan(Object value)
        {
            return is(GREATER_THAN, value);
        }

        public JDBCQueryBuilder<T> isGreaterOrEqual(Object value)
        {
            return is(GREATER_THAN_OR_EQUAL, value);
        }

        public JDBCQueryBuilder<T> like(String pattern)
        {
            return is(LIKE, pattern);
        }

        public JDBCQueryBuilder<T> is(SQLSyntax.SQLOperation operation, Object attribute)
        {
            queryBuilder.getSqlQueryBuilder()
                    .append(operation.toString())
                    .append(ARGUMENT_PLACEHOLDER);

            queryBuilder.addArgument(attribute);

            return queryBuilder;
        }

    }

}
