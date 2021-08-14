package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.NUMERIC;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.TEXT;
import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;


@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataSchema> extends JDBCKeyValueRepository<T, K> implements IObjectStore<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Class<M> metaData;
    private final Set<M> jdbcSchema;

    public JDBCObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> metaData,
            Properties properties
    )
    {
        super(aggregateClazz, keyFunction, properties, false);
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;
        this.metaData = metaData;
        this.jdbcSchema = EnumSet.allOf(metaData);

        autocreateTableObjectStore(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var valueSet = new ArrayList<>();
        List<String> keySet = new ArrayList<>();

        valueSet.add(getJSONConverter().toJson(aggregate));
        jdbcSchema.forEach(element -> valueSet.add(element.getTag().getFromAggregate(aggregate)) );

        keySet.add(KeyValueSchema.VALUE.name());
        jdbcSchema.forEach(element -> keySet.add(element.name()));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(keySet.toArray(new String[0]), valueSet.toArray() )
                .where(KeyValueSchema.KEY).isEqual(getJSONConverter().toJson( keyFunction.apply(aggregate) ))
                .create();

        command.asUpdate();
    }


    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var jsonConverter = getJSONConverter();

        List<String> keySet = new ArrayList<>();
        keySet.add(KeyValueSchema.KEY.name());
        keySet.add(KeyValueSchema.VALUE.name());
        jdbcSchema.forEach(element -> keySet.add(element.name()));


        var objectList = new ArrayList<>();
        objectList.add (jsonConverter.toJson(keyFunction.apply(aggregate)));
        objectList.add (jsonConverter.toJson(aggregate));
        jdbcSchema.forEach(metaTag -> objectList.add(metaTag.getTag().getFromAggregate(aggregate)));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .columns(keySet.toArray(new String[0]))
                .values(objectList.toArray())
                .create();

        command.asUpdate();
    }


    public <S> INumericQuery<T, S> getNumericQuery(M metaTag, Class<S> queryType)
    {
        if (!jdbcSchema.contains(metaTag))
        {
            throw new IllegalArgumentException(metaTag.name() + " is not part of the schema -> Cannot provide a numeric query.");
        }

        if ( !Number.class.isAssignableFrom( metaTag.getTag().getTagType()) )
        {
            throw new IllegalArgumentException(metaTag.name() + " does not use a numeric value -> Could not create a numeric query");
        }

        return new JDBCNumericQuery<>(this::getConnection, metaTag, aggregateClazz, metaData, queryType );
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metaTag, Class<S> queryType)
    {
        if (!jdbcSchema.contains(metaTag))
        {
            throw new IllegalArgumentException(metaTag.name() + " is not part of the schema -> Cannot provide a string query.");
        }

        if ( !String.class.isAssignableFrom( metaTag.getTag().getTagType()) )
        {
            throw new IllegalArgumentException(metaTag.name() + " does not use a numeric value -> Could not create a String query");
        }

        return new JDBCStringQuery<>(this::getConnection, metaTag, aggregateClazz, metaData, queryType );
    }

    private void autocreateTableObjectStore(Properties properties)
    {
        Objects.requireNonNull(properties);
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{

                var command = getConnection().createTableCommand(metaData)
                        .createTableIfNotExists(aggregateClazz)
                        .addColumn(KeyValueSchema.KEY, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)), KeyValueSchema.class)
                        .addConstraint(PRIMARY_KEY)
                        .addColumn(KeyValueSchema.VALUE, TEXT, KeyValueSchema.class);

                jdbcSchema.forEach(element ->
                {
                    if ( Number.class.isAssignableFrom(element.getTag().getTagType()) )
                    {
                        command.addColumn(element, NUMERIC);
                    } else if (String.class.isAssignableFrom(element.getTag().getTagType())){
                        command.addColumn(element, TEXT);
                    } else {
                        throw new IllegalArgumentException("Unsupported Value type " + element.getTag().getTagType().getName() +
                                ". Supported Value types are subtypes of Number and String. ");
                    }
                });

                command.create().asIgnore();

            }
            catch (RuntimeException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
            }
        }
    }
}
