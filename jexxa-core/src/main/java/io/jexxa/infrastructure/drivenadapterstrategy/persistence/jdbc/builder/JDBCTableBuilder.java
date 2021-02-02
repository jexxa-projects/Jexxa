package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.BLANK;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.COMMA;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.CREATE_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.DROP_TABLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.IF_EXISTS;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLSyntax.IF_NOT_EXISTS;

import java.util.function.Supplier;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCCommand;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;

@SuppressWarnings("unused")
public class JDBCTableBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    public JDBCTableBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommand dropTableIfExists(T element)
    {
        getStatementBuilder()
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTableIfExists(Class<?> clazz)
    {
        getStatementBuilder()
                .append(DROP_TABLE)
                .append(IF_EXISTS)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(T element)
    {
        getStatementBuilder()
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(Class<?> clazz)
    {
        getStatementBuilder()
                .append(CREATE_TABLE)
                .append(IF_NOT_EXISTS)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(T element)
    {
        getStatementBuilder()
                .append(CREATE_TABLE)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(Class<?> clazz)
    {
        getStatementBuilder()
                .append(CREATE_TABLE)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCCommand dropTable(T element)
    {
        getStatementBuilder()
                .append(DROP_TABLE)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTable(Class<?> clazz)
    {
        getStatementBuilder()
                .append(DROP_TABLE)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCCommand create()
    {
        return new JDBCCommand(jdbcConnection, getStatementBuilder().toString(), getArguments() );
    }

    public static class JDBCColumnBuilder<T extends Enum<T>>
    {
        private final JDBCTableBuilder<T> commandBuilder;
        private boolean firstColumn = true;

        JDBCColumnBuilder( JDBCTableBuilder<T> commandBuilder )
        {
            this.commandBuilder = commandBuilder;

            this.commandBuilder.getStatementBuilder().append(" ( ");
        }

        public JDBCColumnBuilder<T> addColumn(T element, SQLDataType dataType)
        {
            addCommaSeparatorIfRequired();

            commandBuilder
                    .getStatementBuilder()
                    .append(element.name())
                    .append(BLANK)
                    .append(dataType.toString());

            return this;
        }

        public JDBCColumnBuilder<T> addConstraint( SQLConstraint sqlConstraint)
        {
            commandBuilder
                    .getStatementBuilder()
                    .append(sqlConstraint.toString())
                    .append(BLANK);

            return this;
        }

        public JDBCCommand create()
        {
            commandBuilder.getStatementBuilder()
                    .append(") ");
            return commandBuilder.create();
        }

        private void addCommaSeparatorIfRequired()
        {
            if (!firstColumn)
            {
                commandBuilder.getStatementBuilder().append(COMMA);
            } else {
                firstColumn = false;
            }
        }
    }

    public enum SQLConstraint
    {
        PRIMARY_KEY("PRIMARY KEY");

        private final String string;

        // constructor to set the string
        SQLConstraint(String name){string = name;}

        // the toString just returns the given name
        @Override
        public final String toString() {
            return string;
        }
    }

}
