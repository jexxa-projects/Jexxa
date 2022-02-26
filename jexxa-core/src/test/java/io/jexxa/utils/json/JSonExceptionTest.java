package io.jexxa.utils.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
