package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.IS_NOT_NULL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.IS_NULL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LIKE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.NOT_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.NOT_LIKE;

import java.util.ArrayList;
import java.util.List;

class  JDBCBuilder <T extends Enum<T>>
{
    private final StringBuilder sqlQueryBuilder = new StringBuilder();
    private final List<Object> arguments = new ArrayList<>();


    protected final StringBuilder getSqlQueryBuilder()
    {
        return sqlQueryBuilder;
    }

    protected final void addArgument(Object argument)
    {
        arguments.add(argument);
    }

    protected final List<Object> getArguments()
    {
        return arguments;
    }

    public static class JDBCCondition<V extends Enum<V>, T extends JDBCBuilder<V> >
    {
        private final T queryBuilder;

        JDBCCondition( T queryBuilder)
        {
            this.queryBuilder = queryBuilder;
        }

        public T isEqual(Object value)
        {
            return is(EQUAL, value);
        }

        public T isNull()
        {
            queryBuilder.getSqlQueryBuilder()
                    .append(IS_NULL.toString());

            return queryBuilder;
        }

        public T isNotNull()
        {
            queryBuilder.getSqlQueryBuilder()
                    .append(IS_NOT_NULL.toString());

            return queryBuilder;
        }

        public T isLessThan(Object value)
        {
            return is(LESS_THAN, value);
        }

        public T isLessOrEqual(Object value)
        {
            return is(LESS_THAN_OR_EQUAL, value);
        }

        public T isGreaterThan(Object value)
        {
            return is(GREATER_THAN, value);
        }

        public T isGreaterOrEqual(Object value)
        {
            return is(GREATER_THAN_OR_EQUAL, value);
        }

        public T like(String pattern)
        {
            return is(LIKE, pattern);
        }

        public T notLike(String pattern)
        {
            return is(NOT_LIKE, pattern);
        }

        public T isNotEqual(Object value)
        {
            return is(NOT_EQUAL, value);
        }


        public T is(SQLSyntax.SQLOperation operation, Object attribute)
        {
            queryBuilder.getSqlQueryBuilder()
                    .append(operation.toString())
                    .append(ARGUMENT_PLACEHOLDER);

            queryBuilder.addArgument(attribute);

            return queryBuilder;
        }
    }


}
