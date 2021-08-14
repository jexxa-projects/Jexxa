package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.utils.json.JSONConverter;

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
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
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
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> getDescending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
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
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC_NULLS_LAST)
                .create();

        return searchElements( jdbcQuery );
    }

    public List<T> isNull()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(metaData)
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
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
                .select( JDBCKeyValueRepository.KeyValueSchema.class, JDBCKeyValueRepository.KeyValueSchema.VALUE )
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
                .collect(Collectors.toList());
    }

    protected JDBCConnection getConnection()
    {
        return jdbcConnection.get();
    }

}
