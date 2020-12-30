package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.AssertTrue;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.INTEGRATION_TEST)
class JDBCQueryTest
{
    private JDBCConnection jdbcConnection;

    @BeforeEach
    void initTest()
    {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCConnection.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        jdbcConnection = new JDBCConnection(postgresProperties);
        dropTable();
        autocreateTable(postgresProperties);
    }

    @Test
    void testNonNullValues()
    {
        //Arrange
        var command = String.format("insert into %s values( '%s' , '%s', '%s', '%s', '%s', '%s' )",
                JDBCQueryTest.class.getSimpleName(), 1, 2 , 3, 4, 5, "HELLO WORLD");

        jdbcConnection.execute(command).asUpdate();

        //act
        var getIntegerType =  String.format("select integer_type from %s where key = '1'", JDBCQueryTest.class.getSimpleName());
        jdbcConnection.query(getIntegerType).asInt().findFirst().orElseThrow();
    }

    @Test
    @Disabled
    void testNullStreams()
    {
        //Arrange
        var testStrings = new ArrayList<String>();
        testStrings.add(null);
        testStrings.add("Hello");
        testStrings.add(null);

        var goodStream = testStrings.stream().flatMap(Stream::ofNullable);

        System.out.println("COUNT " + goodStream.count());
        var result = goodStream
                .findFirst()
                .orElseThrow();


        assertNull(result);
    }

    @Test
    @Disabled
    void testNullValues()
    {
        //Arrange
        var command = String.format("insert into %s values( '%s' , null, null, null, null, null )",
                JDBCQueryTest.class.getSimpleName(), 1);

        jdbcConnection.execute(command).asUpdate();

        //act
        var getIntegerType =  String.format("select integer_type from %s where key = '1'", JDBCQueryTest.class.getSimpleName());
        jdbcConnection.query(getIntegerType).asInt().findFirst().orElseThrow();

        var getStringType =  String.format("select string_type from %s where key = '1'", JDBCQueryTest.class.getSimpleName());
        jdbcConnection.query(getStringType).asString().findFirst().orElseThrow();
    }

    @Test
    void testIsPresent()
    {
        //Arrange
        var insertNullValues = String.format("insert into %s values( '%s' , null, null, null, null, null )",
                JDBCQueryTest.class.getSimpleName(), 1);
        var insertNonNullValues = String.format("insert into %s values( '%s' , '%s', '%s', '%s', '%s', '%s' )",
                JDBCQueryTest.class.getSimpleName(), 2, 2 , 3, 4, 5, "HELLO WORLD");

        jdbcConnection.execute(insertNullValues).asUpdate();
        jdbcConnection.execute(insertNonNullValues).asUpdate();

        var queryNullInteger =  String.format("select integer_type from %s where key = '1'", JDBCQueryTest.class.getSimpleName());
        var queryNullString =  String.format("select string_type from %s where key = '1'", JDBCQueryTest.class.getSimpleName());

        var queryNonNullInteger =  String.format("select integer_type from %s where key = '2'", JDBCQueryTest.class.getSimpleName());
        var queryNonNullString =  String.format("select string_type from %s where key = '2'", JDBCQueryTest.class.getSimpleName());

        var queryNotAvailableInteger =  String.format("select integer_type from %s where key = '3'", JDBCQueryTest.class.getSimpleName());
        var queryNotAvailableString =  String.format("select string_type from %s where key = '3'", JDBCQueryTest.class.getSimpleName());

        //act / assert - NullValues
        assertTrue( jdbcConnection.query(queryNullInteger).isPresent());
        assertTrue( jdbcConnection.query(queryNullString).isPresent());

        assertFalse( jdbcConnection.query(queryNullInteger).isEmpty());
        assertFalse( jdbcConnection.query(queryNullString).isEmpty());

        //act / assert - NonNullValues
        assertTrue( jdbcConnection.query(queryNonNullInteger).isPresent());
        assertTrue( jdbcConnection.query(queryNonNullString).isPresent());

        assertFalse( jdbcConnection.query(queryNonNullInteger).isEmpty());
        assertFalse( jdbcConnection.query(queryNonNullString).isEmpty());

        //act / assert - Not available values
        assertFalse( jdbcConnection.query(queryNotAvailableInteger).isPresent());
        assertFalse( jdbcConnection.query(queryNotAvailableString).isPresent());

        assertTrue( jdbcConnection.query(queryNotAvailableInteger).isEmpty());
        assertTrue( jdbcConnection.query(queryNotAvailableString).isEmpty());
    }

    private void autocreateTable(final Properties properties)
    {

        var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key INTEGER PRIMARY KEY" +
                        ", integer_type integer" +
                        ", numeric_type numeric" +
                        ", float_type float" +
                        ", double_type double precision" +
                        ", string_type VARCHAR ) "
                , JDBCQueryTest.class.getSimpleName());

        jdbcConnection
                .execute(command)
                .asIgnore();


    }

    private void dropTable()
    {
        var command = String.format("DROP TABLE IF EXISTS %s ", JDBCQueryTest.class.getSimpleName());

        jdbcConnection
                .execute(command)
                .asIgnore();
    }


}
