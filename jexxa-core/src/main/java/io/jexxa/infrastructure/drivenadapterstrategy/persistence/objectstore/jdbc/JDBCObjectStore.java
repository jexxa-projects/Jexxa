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
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.converter.MetadataConverter;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;


@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataConverter> extends JDBCKeyValueRepository<T, K> implements IObjectStore<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Class<M> comparatorSchema;
    private final Set<M> comparatorFunctions;

    public JDBCObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> comparatorSchema,
            Properties properties
    )
    {
        super(aggregateClazz, keyFunction, properties, false);
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;
        this.comparatorSchema = comparatorSchema;
        this.comparatorFunctions = EnumSet.allOf(comparatorSchema);

        var iterator = comparatorFunctions.iterator();

        autocreateTableObjectStore(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var valueSet = new ArrayList<>();
        List<String> keySet = new ArrayList<>();

        valueSet.add(getJSONConverter().toJson(aggregate));
        comparatorFunctions.forEach( element -> valueSet.add(element.getValueConverter().convertAggregate(aggregate)) );

        keySet.add(KeyValueSchema.VALUE.name());
        comparatorFunctions.forEach(element -> keySet.add(element.name()));

        //"update %s where key = '%s' "  ComparatorValues... " '%s' = '%s' "
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

        var objectList = new ArrayList<>();
        objectList.add (jsonConverter.toJson(keyFunction.apply(aggregate)));
        objectList.add (jsonConverter.toJson(aggregate));
        comparatorFunctions.forEach(metadata -> objectList.add(metadata.getValueConverter().convertAggregate(aggregate)));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .values(objectList.toArray())
                .create();

        command.asUpdate();
    }


    public <S> INumericQuery<T, S> getNumericQuery(M metadata, Class<S> queryType)
    {
        if (!comparatorFunctions.contains(metadata))
        {
            throw new IllegalArgumentException(metadata.name() + " is not part of the schema -> Cannot provide a numeric query.");
        }

        if ( !Number.class.isAssignableFrom( metadata.getValueConverter().getValueType()) )
        {
            throw new IllegalArgumentException(metadata.name() + " does not use a numeric value -> Could not create a numeric query");
        }

        return new JDBCNumericQuery<>(this::getConnection, metadata.getValueConverter(), metadata, aggregateClazz,comparatorSchema, queryType );
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metadata, Class<S> queryType)
    {
        if (!comparatorFunctions.contains(metadata))
        {
            throw new IllegalArgumentException(metadata.name() + " is not part of the schema -> Cannot provide a string query.");
        }

        if ( !String.class.isAssignableFrom( metadata.getValueConverter().getValueType()) )
        {
            throw new IllegalArgumentException(metadata.name() + " does not use a numeric value -> Could not create a String query");
        }

        return new JDBCStringQuery<>(this::getConnection, metadata.getValueConverter(), metadata, aggregateClazz,comparatorSchema, queryType );
    }

    private void autocreateTableObjectStore(Properties properties)
    {
        Objects.requireNonNull(properties);
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{

                var command = getConnection().createTableCommand(comparatorSchema)
                        .createTableIfNotExists(aggregateClazz)
                        .addColumn(KeyValueSchema.KEY, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)), KeyValueSchema.class)
                        .addConstraint(PRIMARY_KEY)
                        .addColumn(KeyValueSchema.VALUE, TEXT, KeyValueSchema.class);

                comparatorFunctions.forEach(element ->
                {
                    if ( Number.class.isAssignableFrom(element.getValueConverter().getValueType()) )
                    {
                        command.addColumn(element, NUMERIC);
                    } else if (String.class.isAssignableFrom(element.getValueConverter().getValueType())){
                        command.addColumn(element, TEXT);
                    } else {
                        throw new IllegalArgumentException("Unsupported Value type " + element.getValueConverter().getValueType().getName() +
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
