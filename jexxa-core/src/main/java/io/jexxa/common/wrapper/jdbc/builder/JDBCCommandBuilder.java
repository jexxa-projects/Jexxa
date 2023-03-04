package io.jexxa.common.wrapper.jdbc.builder;

import io.jexxa.common.wrapper.jdbc.JDBCCommand;
import io.jexxa.common.wrapper.jdbc.JDBCConnection;

import java.util.function.Supplier;

import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.AND;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.BLANK;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.COMMA;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.DELETE;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.FROM;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.INSERT_INTO;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.OR;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.SET;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.UPDATE;
import static io.jexxa.common.wrapper.jdbc.builder.SQLSyntax.WHERE;

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

    public JDBCCommandBuilder<T> values(Object[] args)
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

    public JDBCCommandBuilder<T> values(JDBCObject[] args)
    {
        getStatementBuilder().append("values ( ");
        getStatementBuilder().append( args[0].getBindParameter()); // Handle first entry (without COMMA)
        addArgument(args[0].getJdbcValue());

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( COMMA );
            getStatementBuilder().append( args[i].getBindParameter() );
            addArgument(args[i].getJdbcValue());
        }
        getStatementBuilder().append(")");

        return this;
    }

    public JDBCCommandBuilder<T> columns(String... args)
    {
        getStatementBuilder().append("( ");
        getStatementBuilder().append(args[0]);

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( COMMA );
            getStatementBuilder().append( args[i] );
        }
        getStatementBuilder().append(" ) ");

        return this;
    }

    //CREATE UNIQUE INDEX JexxaInboundMessage_repository_key ON JexxaInboundMessage (repository_key)
    public JDBCCommandBuilder<T> createUniqueIndex(String indexName)
    {
        getStatementBuilder()
                .append("CREATE UNIQUE INDEX ")
                .append(indexName);
        return this;
    }

    public JDBCCommandBuilder<T> createIndex(String indexName)
    {
        getStatementBuilder()
                .append("CREATE INDEX ")
                .append(indexName);
        return this;
    }

    public JDBCCommandBuilder<T> on(String table, String... columns)
    {
        getStatementBuilder()
                .append(" ON ")
                .append(table)
                .append("(")
                .append(columns[0]);

        for(var i = 1;  i < columns.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( COMMA );
            getStatementBuilder().append( columns[i] );
        }
        getStatementBuilder().append(" ) ");

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

    public JDBCCommandBuilder<T> set(T element, JDBCObject value)
    {
        getStatementBuilder()
                .append(SET)
                .append(element.name())
                .append(EQUAL)
                .append(value.getBindParameter());

        addArgument(value.getJdbcValue());
        return this;
    }

    public JDBCCommandBuilder<T> set(String[] element, JDBCObject[] value)
    {
        getStatementBuilder()
                .append(SET);

        for (var i = 0; i < element.length; ++i)
        {
            addArgument(value[i].getJdbcValue());
            getStatementBuilder()
                    .append( element[i] )
                    .append(EQUAL)
                    .append(value[i].getBindParameter());
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
