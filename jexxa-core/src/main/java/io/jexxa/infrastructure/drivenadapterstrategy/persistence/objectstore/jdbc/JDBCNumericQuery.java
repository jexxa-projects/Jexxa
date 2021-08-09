package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.Metadata;

class JDBCNumericQuery<T,S, M extends Enum<M> & Metadata> extends JDBCObjectQuery<T, S, M> implements INumericQuery<T, S>
{
    private final MetaTag<T, S, ? extends Number> numericMetaTag;

    private final Class<T> aggregateClazz;
    private final M nameOfRow;
    private final Class<M> comparatorSchema;

    JDBCNumericQuery(Supplier<JDBCConnection> jdbcConnection,
                     M metaTag,
                     Class<T> aggregateClazz,
                     Class<M> comparatorSchema,
                     Class<S> queryType)
    {
        super(jdbcConnection, metaTag, aggregateClazz, comparatorSchema, queryType);

        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(metaTag);
        this.numericMetaTag = Objects.requireNonNull(metaTag.getMetaTag());
        this.comparatorSchema = Objects.requireNonNull(comparatorSchema);
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        var sqlStartValue = numericMetaTag.convertValue(startValue);

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlStartValue = numericMetaTag.convertValue(value);

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlStartValue = numericMetaTag.convertValue(startValue);
        var sqlEndValue = numericMetaTag.convertValue(endValue);

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlStartValue = numericMetaTag.convertValue(startValue);
        var sqlEndValue = numericMetaTag.convertValue(endValue);

        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlEndValue = numericMetaTag.convertValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlEndValue = numericMetaTag.convertValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlValue = numericMetaTag.convertValue(value);
        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
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
        var sqlValue = numericMetaTag.convertValue(value);
        var jdbcQuery = getConnection()
                .createQuery(comparatorSchema)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotEqual(sqlValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

}

