package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SET;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLConstraint;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.ARGUMENT_PLACEHOLDER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.CREATE_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.DROP_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.FROM;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.IF_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.IF_NOT_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.INSERT_INTO;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.DELETE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.GREATER_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.LESS_THAN_OR_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLOperation.NOT_EQUAL;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.UPDATE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.WHERE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>>
{
    private final StringBuilder sqlCommandBuilder = new StringBuilder();
    private final Supplier<JDBCConnection> jdbcConnection;
    private final List<Object> arguments = new ArrayList<>();


    JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommandBuilder<T> update(T element)
    {
        sqlCommandBuilder
                .append(UPDATE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> update(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(UPDATE)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        sqlCommandBuilder
                .append(INSERT_INTO)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(INSERT_INTO)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> values(Object... args)
    {
        sqlCommandBuilder.append("values ( ");
        for(int i = 0;  i < args.length; ++i )
        {
            sqlCommandBuilder.append( " ? " );
            if ( i < args.length -1)
            {
                sqlCommandBuilder.append( COMMA );
            }
            arguments.add(args[i]);
        }
        sqlCommandBuilder.append(")");

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(T element)
    {
        sqlCommandBuilder
                .append(DELETE)
                .append(FROM)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(DELETE)
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(T element)
    {
        sqlCommandBuilder
                .append(FROM)
                .append( element.name() )
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> from(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(FROM)
                .append(clazz.getSimpleName())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> where(T element)
    {
        sqlCommandBuilder
                .append(WHERE)
                .append(element.name())
                .append(BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> isEqual(Object value)
    {
        return is(EQUAL, value);
    }

    public JDBCCommandBuilder<T> isLessThan(Object value)
    {
        return is(LESS_THAN, value);
    }

    public JDBCCommandBuilder<T> isGreaterThan(Object value)
    {
        return is(GREATER_THAN, value);
    }

    public JDBCCommandBuilder<T> isNotEqual(Object value)
    {
        return is(NOT_EQUAL, value);
    }

    public JDBCCommandBuilder<T> isLessThanOrEqual(Object value)
    {
        return is(LESS_THAN_OR_EQUAL, value);
    }

    public JDBCCommandBuilder<T> isGreaterThanOrEqual(Object value)
    {
        return is(GREATER_THAN_OR_EQUAL, value);
    }


    public String getCommand()
    {
        return sqlCommandBuilder.toString();
    }

    public JDBCCommandBuilder<T> is(SQLSyntax.SQLOperation operation, Object attribute)
    {
        sqlCommandBuilder
                .append(operation.toString())
                .append(ARGUMENT_PLACEHOLDER);

        arguments.add(attribute);
        return this;
    }

    public JDBCCommandBuilder<T> set(T element, Object value)
    {
        sqlCommandBuilder
                .append(SET)
                .append(element.name())
                .append(EQUAL.toString())
                .append(ARGUMENT_PLACEHOLDER);

        arguments.add(value);
        return this;
    }


    public JDBCCommand dropTableIfExists(T element)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTableIfExists(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(T element)
    {
        sqlCommandBuilder
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(T element)
    {
        sqlCommandBuilder
                .append(CREATE_TABLE)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(CREATE_TABLE)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCCommand dropTable(T element)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTable(Class<?> clazz)
    {
        sqlCommandBuilder
                .append(DROP_TABLE)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCCommand create()
    {
        return new JDBCCommand(jdbcConnection, sqlCommandBuilder.toString(), arguments );
    }

    public static class JDBCColumnBuilder<T extends Enum<T>>
    {
        private final JDBCCommandBuilder<T> commandBuilder;
        private boolean firstColumn = true;

        JDBCColumnBuilder( JDBCCommandBuilder<T> commandBuilder )
        {
            this.commandBuilder = commandBuilder;

            this.commandBuilder.getSqlCommandBuilder().append(" ( ");
        }

        public JDBCColumnBuilder<T> addColumn(T element, SQLDataType dataType)
        {
            addCommaSeparatorIfRequired();

            commandBuilder
                    .getSqlCommandBuilder()
                    .append(element.name())
                    .append(BLANK)
                    .append(dataType.toString());

            return this;
        }

        public JDBCColumnBuilder<T> addConstraint( SQLConstraint sqlConstraint)
        {
            commandBuilder
                    .getSqlCommandBuilder()
                    .append(sqlConstraint.toString())
                    .append(BLANK);

            return this;
        }

        public JDBCCommand create()
        {
            commandBuilder.getSqlCommandBuilder()
                    .append(") ");
            return commandBuilder.create();
        }

        private void addCommaSeparatorIfRequired()
        {
            if (!firstColumn)
            {
                commandBuilder.getSqlCommandBuilder().append(COMMA);
            } else {
                firstColumn = false;
            }
        }
    }

    StringBuilder getSqlCommandBuilder()
    {
        return sqlCommandBuilder;
    }
}
