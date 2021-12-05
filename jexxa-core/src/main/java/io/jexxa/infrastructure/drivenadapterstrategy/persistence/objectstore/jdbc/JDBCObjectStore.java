package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database.DatabaseManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database.IDatabase;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc.JDBCKeyValueRepository;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.*;
import static io.jexxa.utils.json.JSONManager.getJSONConverter;


@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataSchema> extends JDBCKeyValueRepository<T, K> implements IObjectStore<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Class<M> metaData;
    private final Set<M> jdbcSchema;

    private final IDatabase database;


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
        this.database = DatabaseManager.getDatabase(properties);

        manageObjectStore(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var valueSet = new ArrayList<JDBCObject>();
        List<String> keySet = new ArrayList<>();

        valueSet.add(valueToJSONB(aggregate));

        jdbcSchema.forEach(element -> valueSet.add( new JDBCObject(
                element.getTag().getFromAggregate(aggregate),
                typeToSQL(element.getTag().getTagType())) ));

        keySet.add(KeyValueSchema.VALUE.name());
        jdbcSchema.forEach(element -> keySet.add(element.name()));

        var jdbcKey = primaryKeyToJSONB(keyFunction.apply(aggregate));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(keySet.toArray(new String[0]), valueSet.toArray(new JDBCObject[0]))
                .where(KeyValueSchema.KEY).isEqual(jdbcKey)
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

        var objectList = new ArrayList<JDBCObject>();
        objectList.add (primaryKeyToJSONB(keyFunction.apply(aggregate)));
        objectList.add (valueToJSONB(aggregate));
        jdbcSchema.forEach(metaTag -> objectList.add(
                new JDBCObject( metaTag.getTag().getFromAggregate(aggregate), typeToSQL(metaTag.getTag().getTagType())))
        );

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .columns(keySet.toArray(new String[0]))
                .values(objectList.toArray(new JDBCObject[0]))
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

    private void manageObjectStore(Properties properties)
    {
        Objects.requireNonNull(properties);
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            autoCreateDatabase();
            alterKeyValueRows();
        }
    }

    private void autoCreateDatabase()
    {
        try{

            var command = getConnection().createTableCommand(metaData)
                    .createTableIfNotExists(aggregateClazz)
                    .addColumn(KeyValueSchema.KEY, database.matchPrimaryKey(JSONB), KeyValueSchema.class)
                    .addConstraint(PRIMARY_KEY)
                    .addColumn(KeyValueSchema.VALUE, database.matchDataType(JSONB), KeyValueSchema.class);

            jdbcSchema.forEach(element -> command.addColumn(element, typeToSQL(element.getTag().getTagType())) );

            command.create().asIgnore();

        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
        }
    }

    private static SQLDataType typeToSQL(Class<?> clazz)
    {
        if ( Number.class.isAssignableFrom(clazz) )
        {
            return NUMERIC;
        }

        else if (String.class.isAssignableFrom(clazz)){
            return TEXT;
        } else {
            throw new IllegalArgumentException("Unsupported Value type " + clazz.getName() + ". Supported Value types are subtypes of Number and String. ");
        }
    }
}
