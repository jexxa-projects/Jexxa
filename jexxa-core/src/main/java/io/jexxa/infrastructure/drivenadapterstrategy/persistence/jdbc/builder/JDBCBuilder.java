package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

import java.util.ArrayList;
import java.util.List;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.SQLOperation.*;

@SuppressWarnings("unused")
public class  JDBCBuilder <T extends Enum<T>>
{
    private final StringBuilder sqlQueryBuilder = new StringBuilder();
    private final List<Object> arguments = new ArrayList<>();

    public String getStatement()
    {
        return getStatementBuilder().toString();
    }

    public final StringBuilder getStatementBuilder()
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

        public JDBCCondition( T queryBuilder)
        {
            this.queryBuilder = queryBuilder;
        }

        public T isEqual(Object value)
        {
            return is(EQUAL, value);
        }

        public T isEqual(JDBCObject value)
        {
            return is(EQUAL, value.getJdbcValue(), value.getBindParameter());
        }

        public T isNull()
        {
            queryBuilder.getStatementBuilder()
                    .append(IS_NULL);

            return queryBuilder;
        }

        public T isNotNull()
        {
            queryBuilder.getStatementBuilder()
                    .append(IS_NOT_NULL);

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
            return is(operation, attribute, ARGUMENT_PLACEHOLDER);
        }

        private T is(SQLSyntax.SQLOperation operation, Object attribute, String argumentPlaceHolder)
        {
            queryBuilder.getStatementBuilder()
                    .append(operation.toString())
                    .append(argumentPlaceHolder);

            queryBuilder.addArgument(attribute);

            return queryBuilder;
        }
    }
}
