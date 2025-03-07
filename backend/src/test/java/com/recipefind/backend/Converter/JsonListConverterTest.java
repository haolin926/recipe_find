package com.recipefind.backend.Converter;

import com.recipefind.backend.converter.JsonListConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonListConverterTest {

    private final JsonListConverter converter = new JsonListConverter();

    @Test
    public void testConvertToDatabaseColumn() {
        List<String> list = List.of("item1", "item2", "item3");
        String expectedJson = "[\"item1\",\"item2\",\"item3\"]";

        String json = converter.convertToDatabaseColumn(list);

        assertEquals(expectedJson, json);
    }

    @Test
    public void testConvertToDatabaseColumn_Null() {
        String json = converter.convertToDatabaseColumn(null);

        assertEquals("[]", json);
    }

    @Test
    public void testConvertToDatabaseColumn_Empty() {
        String json = converter.convertToDatabaseColumn(List.of());

        assertEquals("[]", json);
    }

    @Test
    public void testConvertToEntityAttribute() {
        String json = "[\"item1\",\"item2\",\"item3\"]";
        List<String> expectedList = List.of("item1", "item2", "item3");

        List<String> list = converter.convertToEntityAttribute(json);

        assertEquals(expectedList, list);
    }

    @Test
    public void testConvertToEntityAttribute_Null() {
        List<String> list = converter.convertToEntityAttribute(null);

        assertEquals(List.of(), list);
    }

    @Test
    public void testConvertToEntityAttribute_Empty() {
        List<String> list = converter.convertToEntityAttribute("");

        assertEquals(List.of(), list);
    }

    @Test
    public void testConvertToEntityAttribute_InvalidJson() {
        String invalidJson = "invalid json";

        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute(invalidJson));
    }
}
