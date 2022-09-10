package io.jexxa.utils.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JSonRecordTest
{

    @Test
    void testRecordWithListOfRecords()
    {
        //Arrange
        var objectUnderTest = new RecordWithListOfRecord(
                "stringParam",
                singletonList(new SimpleRecord("stringParam"))
        );

        //Act
        var serializedObject = getJSONConverter().toJson(objectUnderTest);
        var result = getJSONConverter().fromJson(serializedObject, RecordWithListOfRecord.class);

        //Assert
        assertEquals(objectUnderTest, result);
    }


    @Test
    void testRecordWithListOfInteger()
    {
        //Arrange
        var objectUnderTest = new RecordWithListOfInteger("stringParam", List.of(1, 2, 3));

        //Act
        var serializedObject = getJSONConverter().toJson(objectUnderTest);
        var result = getJSONConverter().fromJson(serializedObject, RecordWithListOfInteger.class);

        //Assert
        assertEquals(objectUnderTest, result);
    }


    private record RecordWithListOfRecord(String stringParam, List<SimpleRecord> simpleRecords) { }

    private record SimpleRecord(String stringParam) { }

    private record RecordWithListOfInteger(String stringParam, List<Integer> integers) { }
}