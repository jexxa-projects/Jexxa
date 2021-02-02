package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.AND;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.OR;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ORDER_BY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SELECT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class JDBCQueryBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    private boolean orderByAdded = false;


    JDBCQueryBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCQueryBuilder<T> select(T element)
    {
        getSqlQueryBuilder()
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
                .forEach( entry -> getSqlQueryBuilder()
                        .append(COMMA)
                        .append(entry.name())
                        .append(BLANK)
                );

        return this;
    }

    public JDBCQueryBuilder<T> selectAll()
    {
        getSqlQueryBuilder()
                .append(SELECT)
                .append("* ");
        return this;
    }


    public JDBCQueryBuilder<T> from(T element)
    {
        getSqlQueryBuilder()
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> from(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> where(T element)
    {
        getSqlQueryBuilder()
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> and(T element)
    {
        getSqlQueryBuilder()
                .append(AND)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCQueryBuilder<T>> or(T element)
    {
        getSqlQueryBuilder()
                .append(OR)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public String getQuery()
    {
        return getSqlQueryBuilder().toString();
    }

    public JDBCQuery create()
    {
        return new JDBCQuery(jdbcConnection, getSqlQueryBuilder().toString(), getArguments());
    }

    public JDBCQueryBuilder<T> orderBy(T element, SQLSyntax.SQLOrder order)
    {
        if (!orderByAdded)
        {
            getSqlQueryBuilder().append(ORDER_BY);
            orderByAdded = true;
        }

        getSqlQueryBuilder().append(element.name())
                .append(BLANK)
                .append(order.name())
                .append(BLANK);

        return this;
    }

    public JDBCQueryBuilder<T> orderBy(T element)
    {
        if (!orderByAdded)
        {
            getSqlQueryBuilder().append(ORDER_BY);
            orderByAdded = true;
        }

        getSqlQueryBuilder().append(element.name())
                .append(BLANK);

        return this;
    }


}
