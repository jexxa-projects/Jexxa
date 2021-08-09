package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.Converter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.MetadataConverter;

public class JDBCStringQuery <T, S, M extends Enum<M> & MetadataConverter> extends JDBCObjectQuery<T, S, M> implements IStringQuery<T, S>
{
    private final Converter<T, S, ? extends String> stringConverter;

    private final Class<T> aggregateClazz;
    private final M nameOfRow;
    private final Class<M> comparatorSchema;

    public JDBCStringQuery(
            Supplier<JDBCConnection> jdbcConnection,
            Converter<T, S, ? extends String> stringConverter,
            M nameOfRow,
            Class<T> aggregateClazz,
            Class<M> comparatorSchema,
            Class<S> queryType
    )
    {
        super(jdbcConnection, nameOfRow, aggregateClazz, comparatorSchema, queryType);

        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(nameOfRow);
        this.stringConverter = Objects.requireNonNull(stringConverter);
        this.comparatorSchema = Objects.requireNonNull(comparatorSchema);
    }

    @Override
    public List<T> beginsWith(S value)
    {
        var sqlStartValue = stringConverter.convertValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .like(sqlStartValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> endsWith(S value)
    {
        var sqlEndValue = "%" + stringConverter.convertValue(value);

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .like(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> includes(S value)
    {
        var sqlIncludeValue = "%" + stringConverter.convertValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .like(sqlIncludeValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        var sqlEqualValue = stringConverter.convertValue(value) ;

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .like(sqlEqualValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> notIncludes(S value)
    {
        var sqlIncludeValue = "%" + stringConverter.convertValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .notLike(sqlIncludeValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }
}
