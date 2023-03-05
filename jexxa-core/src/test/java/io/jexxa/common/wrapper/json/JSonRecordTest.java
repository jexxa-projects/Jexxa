package io.jexxa.common.wrapper.json;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.jexxa.common.wrapper.json.JSONManager.getJSONConverter;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SameParameterValue")
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
    void testRecordWithMapOfRecords()
    {
        //Arrange
        HashMap<String, SimpleRecord> map = new HashMap<>();
        map.put("object1", new SimpleRecord("simpleRecord1"));
        map.put("object2", new SimpleRecord("simpleRecord2"));

        var objectUnderTest = new RecordWithMapOfRecord("stringParam", map);

        //Act
        var serializedObject = getJSONConverter().toJson(objectUnderTest);
        var result = getJSONConverter().fromJson(serializedObject, RecordWithMapOfRecord.class);

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

    private record RecordWithMapOfRecord(String stringParam, Map<String, SimpleRecord> simpleRecordMap) { }
}