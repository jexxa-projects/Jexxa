package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.AND;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.DELETE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.INSERT_INTO;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.OR;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.SET;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.UPDATE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.WHERE;

import java.util.function.Supplier;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCCommand;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    public JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommandBuilder<T> update(T element)
    {
        getStatementBuilder()
                .append(UPDATE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> update(Class<?> clazz)
    {
        getStatementBuilder()
                .append(UPDATE)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        getStatementBuilder()
                .append(INSERT_INTO)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        getStatementBuilder()
                .append(INSERT_INTO)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> values(Object... args)
    {
        getStatementBuilder().append("values ( ");
        getStatementBuilder().append( ARGUMENT_PLACEHOLDER ); // Handle first entry (without COMMA)
        addArgument(args[0]);

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( COMMA );
            getStatementBuilder().append( ARGUMENT_PLACEHOLDER );
            addArgument(args[i]);
        }
        getStatementBuilder().append(")");

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(T element)
    {
        getStatementBuilder()
                .append(DELETE)
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(Class<?> clazz)
    {
        getStatementBuilder()
                .append(DELETE)
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> where(T element)
    {
        getStatementBuilder()
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> and(T element)
    {
        getStatementBuilder()
                .append(AND)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> or(T element)
    {
        getStatementBuilder()
                .append(OR)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCommandBuilder<T> set(T element, Object value)
    {
        getStatementBuilder()
                .append(SET)
                .append(element.name())
                .append(EQUAL)
                .append(ARGUMENT_PLACEHOLDER);

        addArgument(value);
        return this;
    }

    public JDBCCommandBuilder<T> set(String[] element, Object[] value)
    {
        getStatementBuilder()
                .append(SET);

        for (var i = 0; i < element.length; ++i)
        {
            addArgument(value[i]);
            getStatementBuilder()
                    .append( element[i] )
                    .append(EQUAL)
                    .append(ARGUMENT_PLACEHOLDER);
            if ( i < element.length - 1)
            {
                getStatementBuilder().append(COMMA);
            }
        }
        return this;
    }

    public JDBCCommand create()
    {
        return new JDBCCommand(jdbcConnection, getStatementBuilder().toString(), getArguments() );
    }
}
