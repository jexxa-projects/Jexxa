package io.jexxa.common.wrapper.json;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSonExceptionTest {
    private final JSONConverter objectUnderTest = JSONManager.getJSONConverter();

    @Test
    void testCheckedException()
    {
        //Arrange
        var checkedException = new IOException("Test with checked exception");

        //Act
        var serializedResult = objectUnderTest.toJson(checkedException);
        var deserializeResult = objectUnderTest.fromJson(serializedResult, IOException.class);

        //Assert
        assertEquals(checkedException.getMessage(), deserializeResult.getMessage());
    }

    @Test
    void testUncheckedException()
    {
        //Arrange
        var checkedException = new RuntimeException("Test with unchecked exception");

        //Act
        var serializedResult = objectUnderTest.toJson(checkedException);
        var deserializeResult = objectUnderTest.fromJson(serializedResult, RuntimeException.class);

        //Assert
        assertEquals(checkedException.getMessage(), deserializeResult.getMessage());
    }

    @Test
    void testUnknownException()
    {
        //Arrange
        var gson = new Gson();
        var exception = new DummyException("Exception with unknown elements", null);

        //Act
        var serializedResult = gson.toJson(exception);
        var deserializeResult = objectUnderTest.fromJson(serializedResult, Exception.class);

        //Assert
        assertTrue(deserializeResult.getMessage().isEmpty());
    }

    record DummyException(String detailMessage, Object backtrace){}
}
