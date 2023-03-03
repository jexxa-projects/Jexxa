package io.jexxa.infrastructure.persistence.objectstore.jdbc;

import io.jexxa.infrastructure.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.infrastructure.persistence.repository.jdbc.JDBCKeyValueRepository;
import io.jexxa.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.common.wrapper.jdbc.JDBCQuery;
import io.jexxa.common.wrapper.jdbc.builder.SQLOrder;
import io.jexxa.common.wrapper.json.JSONConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static io.jexxa.common.wrapper.json.JSONManager.getJSONConverter;

@SuppressWarnings("unused")
class JDBCObjectQuery <T, S, M extends Enum<M> & MetadataSchema>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    private final Class<T> aggregateClazz;
    private final JSONConverter jsonConverter = getJSONConverter();
    private final M nameOfRow;
    private final Class<M> metaData;

    public JDBCObjectQuery(
            Supplier<JDBCConnection> jdbcConnection,
            M nameOfRow,
            Class<T> aggregateClazz,
            Class<M> metaData,
            Class<S> queryType
    )
    {
        this.jdbcConnection = Objects.requireNonNull( jdbcConnection );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(nameOfRow);
        this.metaData = Objects.requireNonNull(metaData);
        //Type required for java type inference
        Objects.requireNonNull(queryType);
    }

    public List<T> getAscending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .limit(amount)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> getAscending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> getDescending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC_NULLS_LAST)
                .limit(amount)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> getDescending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC_NULLS_LAST)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> isNull()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNull()
                .create();

        return searchElements(jdbcQuery);
    }

    public List<T> isNotNull()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotNull()
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    protected List<T> searchElements(JDBCQuery query)
    {
        return query.asString()
                .flatMap(Optional::stream)
                .map( element -> jsonConverter.fromJson(element, aggregateClazz))
                .toList();
    }

    protected JDBCConnection getConnection()
    {
        return jdbcConnection.get();
    }

}
