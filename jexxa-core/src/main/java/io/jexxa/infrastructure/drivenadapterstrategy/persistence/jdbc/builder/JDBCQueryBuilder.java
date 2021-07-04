package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.AND;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.LIMIT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.OR;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.ORDER_BY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.SELECT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.SELECT_COUNT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.WHERE;

import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;

@SuppressWarnings("unused")
public class JDBCQueryBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    private boolean orderByAdded = false;


    public JDBCQueryBuilder(Supplier<JDBCConnection> jdbcConnection)
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCQueryBuilder<T> select(T element)
    {
        getStatementBuilder()
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
                .forEach( entry -> getStatementBuilder()
                        .append(COMMA)
                        .append(entry.name())
                        .append(BLANK)
                );

        return this;
    }

    public JDBCQueryBuilder<T> selectAll()
    {
        getStatementBuilder()
                .append(SELECT)
                .append("* ");
        return this;
    }

    public JDBCQueryBuilder<T> selectCount(T element)
    {
        getStatementBuilder()
                .append(SELECT_COUNT)
                .append("( ")
                .append(element)
                .append(" )");
        return this;
    }

    public JDBCQueryBuilder<T> selectCount()
    {
        getStatementBuilder()
                .append(SELECT_COUNT)
                .append("( * )");
        return this;
    }

    public JDBCQueryBuilder<T> from(T element)
    {
        getStatementBuilder()
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> from(Class<?> clazz)
    {
        getStatementBuilder()
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> where(T element)
    {
        getStatementBuilder()
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> and(T element)
    {
        getStatementBuilder()
                .append(AND)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> or(T element)
    {
        getStatementBuilder()
                .append(OR)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCQueryBuilder<T> limit(int number)
    {
        getStatementBuilder()
                .append( LIMIT )
                .append( ARGUMENT_PLACEHOLDER )
                .append(BLANK);

        addArgument(number);

        return this;
    }

    public JDBCQuery create()
    {
        return new JDBCQuery(jdbcConnection, getStatementBuilder().toString(), getArguments());
    }

    public JDBCQueryBuilder<T> orderBy(T element, SQLOrder order)
    {
        if (!orderByAdded)
        {
            getStatementBuilder().append(ORDER_BY);
            orderByAdded = true;
        }

        getStatementBuilder().append(element.name())
                .append(BLANK)
                .append(order.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> orderBy(T element)
    {
        if (!orderByAdded)
        {
            getStatementBuilder().append(ORDER_BY);
            orderByAdded = true;
        }

        getStatementBuilder().append(element.name())
                .append(BLANK);

        return this;
    }


}
