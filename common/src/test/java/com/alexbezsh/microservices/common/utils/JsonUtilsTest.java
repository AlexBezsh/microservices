package com.alexbezsh.microservices.common.utils;

import com.alexbezsh.microservices.common.model.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilsTest {

    private static final String JSON_AND_POJO_MUST_NOT_BE_NULL =
        "JSON string and POJO class must not be null";

    @Test
    void toJson() {
        String expected = "{\"testObject\":null}";

        String actual = JsonUtils.toJson(new TestClass1());

        assertEquals(expected, actual);
    }

    @Test
    void toJsonShouldThrowRuntimeExceptionIfObjectIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> JsonUtils.toJson(null));

        assertEquals("Unable to convert null object to JSON", exception.getMessage());
    }

    @Test
    void toJsonShouldThrowRuntimeExceptionIfConversionFails() {
        TestClass1 testObject1 = new TestClass1();
        TestClass2 testObject2 = new TestClass2(testObject1);
        testObject1.setTestObject(testObject2);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> JsonUtils.toJson(testObject1));

        assertTrue(exception.getMessage().startsWith("Failed to convert POJO to JSON. " +
            "Reason: Infinite recursion (StackOverflowError)"));
    }


    @Test
    void fromJson() {
        JsonUtils.fromJson("{}", ErrorResponse.class);
    }

    @Test
    void fromJsonShouldThrowRuntimeExceptionIfJsonIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> JsonUtils.fromJson(null, ErrorResponse.class));

        assertEquals(JSON_AND_POJO_MUST_NOT_BE_NULL, exception.getMessage());
    }

    @Test
    void fromJsonShouldThrowRuntimeExceptionIfTypeIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> JsonUtils.fromJson("{}", null));

        assertEquals(JSON_AND_POJO_MUST_NOT_BE_NULL, exception.getMessage());
    }

    @Test
    void fromJsonShouldThrowRuntimeExceptionIfConversionFails() {
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> JsonUtils.fromJson("}", ErrorResponse.class));

        assertTrue(exception.getMessage().startsWith("Failed to convert JSON to ErrorResponse. " +
            "Reason: Unexpected close marker '}'"));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestClass1 {
        private TestClass2 testObject;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestClass2 {
        private TestClass1 testObject;
    }

}
