package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.AND;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.CREATE_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.DELETE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.DROP_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.IF_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.IF_NOT_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.INSERT_INTO;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.OR;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SET;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLConstraint;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.UPDATE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommandBuilder<T> update(T element)
    {
        getSqlQueryBuilder()
                .append(UPDATE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> update(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(UPDATE)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        getSqlQueryBuilder()
                .append(INSERT_INTO)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(INSERT_INTO)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> values(Object... args)
    {
        getSqlQueryBuilder().append("values ( ");
        getSqlQueryBuilder().append( ARGUMENT_PLACEHOLDER ); // Handle first entry (without COMMA)
        addArgument(args[0]);

        for(int i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getSqlQueryBuilder().append( COMMA );
            getSqlQueryBuilder().append( ARGUMENT_PLACEHOLDER );
            addArgument(args[i]);
        }
        getSqlQueryBuilder().append(")");

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(T element)
    {
        getSqlQueryBuilder()
                .append(DELETE)
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(DELETE)
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(T element)
    {
        getSqlQueryBuilder()
                .append(FROM)
                .append( element.name() )
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> where(T element)
    {
        getSqlQueryBuilder()
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> and(T element)
    {
        getSqlQueryBuilder()
                .append(AND)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> or(T element)
    {
        getSqlQueryBuilder()
                .append(OR)
                .append(element.name())
                .append(BLANK);

        return new JDBCCondition<>(this);
    }


    public String getCommand()
    {
        return getSqlQueryBuilder().toString();
    }


    public JDBCCommandBuilder<T> set(T element, Object value)
    {
        getSqlQueryBuilder()
                .append(SET)
                .append(element.name())
                .append(EQUAL.toString())
                .append(ARGUMENT_PLACEHOLDER);

        addArgument(value);
        return this;
    }


    public JDBCCommand dropTableIfExists(T element)
    {
        getSqlQueryBuilder()
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTableIfExists(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(T element)
    {
        getSqlQueryBuilder()
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(T element)
    {
        getSqlQueryBuilder()
                .append(CREATE_TABLE)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(CREATE_TABLE)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCCommand dropTable(T element)
    {
        getSqlQueryBuilder()
                .append(DROP_TABLE)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTable(Class<?> clazz)
    {
        getSqlQueryBuilder()
                .append(DROP_TABLE)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCCommand create()
    {
        return new JDBCCommand(jdbcConnection, getSqlQueryBuilder().toString(), getArguments() );
    }

    public static class JDBCColumnBuilder<T extends Enum<T>>
    {
        private final JDBCCommandBuilder<T> commandBuilder;
        private boolean firstColumn = true;

        JDBCColumnBuilder( JDBCCommandBuilder<T> commandBuilder )
        {
            this.commandBuilder = commandBuilder;

            this.commandBuilder.getSqlQueryBuilder().append(" ( ");
        }

        public JDBCColumnBuilder<T> addColumn(T element, SQLDataType dataType)
        {
            addCommaSeparatorIfRequired();

            commandBuilder
                    .getSqlQueryBuilder()
                    .append(element.name())
                    .append(BLANK)
                    .append(dataType.toString());

            return this;
        }

        public JDBCColumnBuilder<T> addConstraint( SQLConstraint sqlConstraint)
        {
            commandBuilder
                    .getSqlQueryBuilder()
                    .append(sqlConstraint.toString())
                    .append(BLANK);

            return this;
        }

        public JDBCCommand create()
        {
            commandBuilder.getSqlQueryBuilder()
                    .append(") ");
            return commandBuilder.create();
        }

        private void addCommaSeparatorIfRequired()
        {
            if (!firstColumn)
            {
                commandBuilder.getSqlQueryBuilder().append(COMMA);
            } else {
                firstColumn = false;
            }
        }
    }

}
