package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;

class JDBCNumericQuery<T,S, M extends Enum<M> & MetadataSchema> extends JDBCObjectQuery<T, S, M> implements INumericQuery<T, S>
{
    private final MetaTag<T, S, ? extends Number> numericTag;

    private final Class<T> aggregateClazz;
    private final M nameOfRow;
    private final Class<M> metaDataSchema;

    JDBCNumericQuery(Supplier<JDBCConnection> jdbcConnection,
                     M metaData,
                     Class<T> aggregateClazz,
                     Class<M> metaDataSchema,
                     Class<S> queryType)
    {
        super(jdbcConnection, metaData, aggregateClazz, metaDataSchema, queryType);

        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(metaData);
        this.numericTag = Objects.requireNonNull(metaData.getTag());
        this.metaDataSchema = Objects.requireNonNull(metaDataSchema);
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        var sqlStartValue = numericTag.getFromValue(startValue);

        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        var sqlStartValue = numericTag.getFromValue(value);

        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterThan(sqlStartValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getRangeClosed(S startValue, S endValue)
    {
        var sqlStartValue = numericTag.getFromValue(startValue);
        var sqlEndValue = numericTag.getFromValue(endValue);

        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .and(nameOfRow)
                .isLessOrEqual(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();


        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getRange(S startValue, S endValue)
    {
        var sqlStartValue = numericTag.getFromValue(startValue);
        var sqlEndValue = numericTag.getFromValue(endValue);

        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .and(nameOfRow)
                .isLessThan(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();


        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isLessOrEqualThan(S endValue)
    {
        var sqlEndValue = numericTag.getFromValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isLessOrEqual(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isLessThan(S endValue)
    {
        var sqlEndValue = numericTag.getFromValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isLessThan(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        var sqlValue = numericTag.getFromValue(value);
        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isEqual(sqlValue)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isNotEqualTo(S value)
    {
        var sqlValue = numericTag.getFromValue(value);
        var jdbcQuery = getConnection()
                .createQuery(metaDataSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotEqual(sqlValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

}

