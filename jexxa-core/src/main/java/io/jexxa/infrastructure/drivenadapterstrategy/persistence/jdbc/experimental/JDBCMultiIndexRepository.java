package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection.JDBC_URL;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class JDBCMultiIndexRepository<T,K, M extends Enum<M> & SearchStrategy> extends JDBCRepository implements IMultiIndexRepository<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCMultiIndexRepository.class);
    private static final String SQL_NUMERIC = "NUMERIC";

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Set<M> comparatorFunctions;



    public JDBCMultiIndexRepository(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Set<M> comparatorFunctions,
            Properties properties
    )
    {
        super(properties);
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;
        this.comparatorFunctions = comparatorFunctions;

        autocreateTable(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var stringBuilder = new StringBuilder();

        //Create static part
        stringBuilder.append( String.format(
                "update %s set value = '%s' where key = '%s' "
                , getAggregateName()
                , gson.toJson(aggregate)
                , gson.toJson(keyFunction.apply(aggregate)))
        );

        //Create dynamic part
        comparatorFunctions.forEach( element -> stringBuilder.append(
                String.format(" '%s' = '%s' "
                        , element.name()
                        , element.get().getIntValueT(aggregate))
                )
        );

        getConnection()
                .execute(stringBuilder.toString())
                .asUpdate();
    }

    @Override
    public void remove(K key)
    {
        Objects.requireNonNull(key);

        String command = String.format("delete from %s where key= '%s'"
                , getAggregateName()
                , gson.toJson(key));

        getConnection()
                .execute(command)
                .asUpdate();
    }

    @Override
    public void removeAll()
    {
        String command = String.format("delete from %s", getAggregateName());

        getConnection()
                .execute(command)
                .asIgnore();
    }

    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        //Create inner part in ( )
        var stringBuilder = new StringBuilder();
        stringBuilder.append(String.format( " '%s' , '%s' "
                , gson.toJson(keyFunction.apply(aggregate))
                , gson.toJson(aggregate))
        );

        comparatorFunctions.forEach( element -> stringBuilder.append(
                String.format(", '%s'", element.get().getIntValueT(aggregate))));

        String command = String.format("insert into %s values( %s )"
                , getAggregateName()
                , stringBuilder.toString());

        getConnection()
                .execute(command)
                .asUpdate();
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        Objects.requireNonNull(primaryKey);

        String sqlQuery = String.format( "select value from %s where key = '%s'"
                , getAggregateName()
                , gson.toJson(primaryKey));

        return getConnection()
                .query(sqlQuery)
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map( element -> gson.fromJson(element, aggregateClazz))
                .or(Optional::empty);
    }

    @Override
    public List<T> get()
    {
        return getConnection()
                .query("select value from "+ getAggregateName())
                .asString()
                .flatMap(Optional::stream)
                .map( element -> gson.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());

    }


    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{
                //Create inner part in ( )
                var stringBuilder = new StringBuilder();
                comparatorFunctions.forEach( element -> stringBuilder.append(
                        String.format(", %s  %s ", element.name(), SQL_NUMERIC )));

                var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key VARCHAR %s PRIMARY KEY, value text %s ) "
                        , aggregateClazz.getSimpleName()
                        , getMaxVarChar(properties.getProperty(JDBC_URL))
                        , stringBuilder.toString());

                getConnection()
                        .execute(command)
                        .asIgnore();
            }
            catch (RuntimeException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", getAggregateName());
            }
        }
    }

    protected String getAggregateName()
    {
        return aggregateClazz.getSimpleName();
    }

    private static String getMaxVarChar(String jdbcDriver)
    {
        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return "(4000)";
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return ""; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return "(" + Integer.MAX_VALUE + ")";
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return "(65535)";
        }

        return "(255)";
    }


    public <S> IRangeQuery<T, S> getRangeQuery(M strategy)
    {
        if ( !comparatorFunctions.contains(strategy) )
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }
        return new JDBCRangeQuery<>(this, strategy.get(), strategy.name(), aggregateClazz);
    }
}
