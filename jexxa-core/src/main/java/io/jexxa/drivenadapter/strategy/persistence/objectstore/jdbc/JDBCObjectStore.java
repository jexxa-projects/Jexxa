package io.jexxa.drivenadapter.strategy.persistence.objectstore.jdbc;

import io.jexxa.drivenadapter.strategy.persistence.objectstore.INumericQuery;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.IObjectStore;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.IStringQuery;
import io.jexxa.drivenadapter.strategy.persistence.objectstore.metadata.MetadataSchema;
import io.jexxa.drivenadapter.strategy.persistence.repository.jdbc.JDBCKeyValueRepository;
import io.jexxa.api.wrapper.jdbc.builder.JDBCObject;
import io.jexxa.api.wrapper.jdbc.builder.SQLDataType;
import io.jexxa.api.wrapper.jdbc.database.DatabaseManager;
import io.jexxa.api.wrapper.jdbc.database.IDatabase;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.properties.JexxaJDBCProperties;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import static io.jexxa.api.wrapper.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.api.wrapper.jdbc.builder.SQLDataType.JSONB;
import static io.jexxa.api.wrapper.jdbc.builder.SQLDataType.NUMERIC;
import static io.jexxa.api.wrapper.jdbc.builder.SQLDataType.TEXT;


@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataSchema> extends JDBCKeyValueRepository<T, K> implements IObjectStore<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;

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
        this.database = DatabaseManager.getDatabase(properties.getProperty(JexxaJDBCProperties.JEXXA_JDBC_URL));

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

        keySet.add(KeyValueSchema.REPOSITORY_VALUE.name());
        jdbcSchema.forEach(element -> keySet.add(element.name()));

        var jdbcKey = primaryKeyToJSONB(keyFunction.apply(aggregate));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(keySet.toArray(new String[0]), valueSet.toArray(new JDBCObject[0]))
                .where(KeyValueSchema.REPOSITORY_KEY).isEqual(jdbcKey)
                .create();

        command.asUpdate();
    }


    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        List<String> keySet = new ArrayList<>();
        keySet.add(KeyValueSchema.REPOSITORY_KEY.name());
        keySet.add(KeyValueSchema.REPOSITORY_VALUE.name());
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
        if (properties.containsKey(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE))
        {
            autoCreateDatabase();
            renameKeyValueColumns();
            alterKeyValueRows();
        }
    }

    private void autoCreateDatabase()
    {
        try{

            var command = getConnection().createTableCommand(metaData)
                    .createTableIfNotExists(aggregateClazz)
                    .addColumn(KeyValueSchema.REPOSITORY_KEY, database.matchingPrimaryKey(JSONB), KeyValueSchema.class)
                    .addConstraint(PRIMARY_KEY)
                    .addColumn(KeyValueSchema.REPOSITORY_VALUE, database.matchingValue(JSONB), KeyValueSchema.class);

            jdbcSchema.forEach(element -> command.addColumn(element, typeToSQL(element.getTag().getTagType())) );

            command.create().asIgnore();

        }
        catch (RuntimeException e)
        {
            LOGGER.debug("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
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
