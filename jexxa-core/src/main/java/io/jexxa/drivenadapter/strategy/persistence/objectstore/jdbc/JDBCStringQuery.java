package io.jexxa.drivenadapter.strategy.persistence.objectstore.jdbc;

import io.jexxa.api.wrapper.jdbc.JDBCConnection;
import io.jexxa.api.wrapper.jdbc.builder.SQLOrder;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.IStringQuery;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.metadata.MetaTag;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.drivenadapter.strategy.persistence.repository.jdbc.JDBCKeyValueRepository;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class JDBCStringQuery <T, S, M extends Enum<M> & MetadataSchema> extends JDBCObjectQuery<T, S, M> implements IStringQuery<T, S>
{
    private final MetaTag<T, S, String> stringMetaTag;

    private final Class<T> aggregateClazz;
    private final M nameOfRow;
    private final Class<M> metaData;

    public JDBCStringQuery(
            Supplier<JDBCConnection> jdbcConnection,
            M metaTag,
            Class<T> aggregateClazz,
            Class<M> metaData,
            Class<S> queryType
    )
    {
        super(jdbcConnection, metaTag, aggregateClazz, metaData, queryType);

        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(metaTag);
        this.stringMetaTag = nameOfRow.getTag();
        this.metaData = Objects.requireNonNull(metaData);
    }

    @Override
    public List<T> beginsWith(S value)
    {
        var sqlStartValue = stringMetaTag.getFromValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
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
        var sqlEndValue = "%" + stringMetaTag.getFromValue(value);

        var jdbcQuery = getConnection()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
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
        var sqlIncludeValue = "%" + stringMetaTag.getFromValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
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
        var sqlEqualValue = stringMetaTag.getFromValue(value) ;

        var jdbcQuery = getConnection()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
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
        var sqlIncludeValue = "%" + stringMetaTag.getFromValue(value) + "%";

        var jdbcQuery = getConnection()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .where(nameOfRow)
                .notLike(sqlIncludeValue)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }
}
