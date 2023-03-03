package io.jexxa.api.wrapper.jdbc.builder;

import io.jexxa.api.wrapper.jdbc.JDBCCommand;
import io.jexxa.api.wrapper.jdbc.JDBCConnection;

import java.util.function.Supplier;

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
                .append(SQLSyntax.DROP_TABLE)
                .append(SQLSyntax.IF_EXISTS)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTableIfExists(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.DROP_TABLE)
                .append(SQLSyntax.IF_EXISTS)
                .append(clazz.getSimpleName());

        return create();
    }

    public JDBCColumnBuilder<T> alterTable(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.ALTER_TABLE)
                .append(clazz.getSimpleName())
                .append(SQLSyntax.BLANK);
        return new JDBCColumnBuilder<>(this);
    }


    public JDBCColumnBuilder<T> createTableIfNotExists(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.CREATE_TABLE)
                .append(SQLSyntax.IF_NOT_EXISTS)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTableIfNotExists(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.CREATE_TABLE)
                .append(SQLSyntax.IF_NOT_EXISTS)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.CREATE_TABLE)
                .append(element.name());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCColumnBuilder<T> createTable(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.CREATE_TABLE)
                .append(clazz.getSimpleName());

        return new JDBCColumnBuilder<>(this);
    }

    public JDBCCommand dropTable(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.DROP_TABLE)
                .append(element.name());

        return create();
    }

    public JDBCCommand dropTable(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.DROP_TABLE)
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
        private boolean openBraces = false;

        JDBCColumnBuilder( JDBCTableBuilder<T> commandBuilder )
        {
            this.commandBuilder = commandBuilder;
        }

        public JDBCTableBuilder<T> alterColumn(T element, SQLDataType newDataType )
        {
            return alterColumn(element, newDataType, "");
        }

        public JDBCTableBuilder<T> alterColumn(String element, SQLDataType newDataType )
        {
            return alterColumn(element, newDataType, "");
        }

        public JDBCTableBuilder<T> alterColumn(T element, SQLDataType newDataType, String usingStatement )
        {
            return alterColumn(element.name(), newDataType, usingStatement);
        }

        public JDBCTableBuilder<T> alterColumn(String element, SQLDataType newDataType, String usingStatement )
        {
            addCommaSeparatorIfRequired();

            commandBuilder
                    .getStatementBuilder()
                    .append(SQLSyntax.ALTER_COLUMN)
                    .append(element)
                    .append(SQLSyntax.BLANK)
                    .append(SQLSyntax.TYPE)
                    .append(newDataType.toString())
                    .append(usingStatement);

            return commandBuilder;
        }

        public JDBCColumnBuilder<T> addColumn(T element, SQLDataType dataType)
        {
            addCommaSeparatorIfRequired();
            openBracesIfRequired();

            commandBuilder
                    .getStatementBuilder()
                    .append(element.name())
                    .append(SQLSyntax.BLANK)
                    .append(dataType.toString());

            return this;
        }

        public <S extends Enum<S>> JDBCColumnBuilder<T> addColumn(S element, SQLDataType dataType, Class<S> schemaClass)
        {
            addCommaSeparatorIfRequired();
            openBracesIfRequired();

            commandBuilder
                    .getStatementBuilder()
                    .append(element.name())
                    .append(SQLSyntax.BLANK)
                    .append(dataType.toString());

            return this;
        }


        public JDBCColumnBuilder<T> addConstraint( SQLConstraint sqlConstraint)
        {
            commandBuilder
                    .getStatementBuilder()
                    .append(sqlConstraint.toString())
                    .append(SQLSyntax.BLANK);

            return this;
        }

        public JDBCCommand create()
        {
            closeBracesIfRequired();
            return commandBuilder.create();
        }

        private void addCommaSeparatorIfRequired()
        {
            if (firstColumn)
            {
                firstColumn = false;
            } else {
                commandBuilder.getStatementBuilder().append(SQLSyntax.COMMA);
            }
        }

        private void openBracesIfRequired()
        {
            if (!openBraces)
            {
                commandBuilder.getStatementBuilder().append("( ");
                openBraces = true;
            }
        }

        private void closeBracesIfRequired()
        {
            if (openBraces)
            {
                commandBuilder.getStatementBuilder().append(" )");
                openBraces = false;
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
