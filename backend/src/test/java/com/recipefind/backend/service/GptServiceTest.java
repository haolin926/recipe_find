package com.recipefind.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.Impl.GptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GptServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GptServiceImpl gptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructRecipeDescription_Success() throws Exception {
        // Given
        List<RecipeDTO> recipes = new ArrayList<>();
        RecipeDTO recipe1 = new RecipeDTO();
        recipe1.setName("Spaghetti Bolognese");
        recipes.add(recipe1);

        RecipeDTO recipe2 = new RecipeDTO();
        recipe2.setName("Chicken Curry");
        recipes.add(recipe2);

        String responseBody = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"recipe\\\": \\\"Spaghetti Bolognese\\\", \\\"description\\\": \\\"A classic Italian pasta dish...\\\"}, {\\\"recipe\\\": \\\"Chicken Curry\\\", \\\"description\\\": \\\"A spicy and flavorful dish...\\\"}]\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Mocking ObjectMapper behavior for different inputs
        ObjectMapper realObjectMapper = new ObjectMapper();

        when(objectMapper.readTree(anyString())).thenAnswer(invocation -> {
            String jsonInput = invocation.getArgument(0, String.class);

            if (jsonInput.equals(responseBody)) {
                // This is the first call - parsing full API response
                return realObjectMapper.readTree(responseBody);
            } else {
                // This is the second call - parsing GPT's returned JSON array
                return realObjectMapper.readTree("""
            [
                {"recipe": "Spaghetti Bolognese", "description": "A classic Italian pasta dish..."},
                {"recipe": "Chicken Curry", "description": "A spicy and flavorful dish..."}
            ]
            """);
            }
        });
        // When
        List<RecipeDTO> result = gptService.constructRecipeDescription(recipes);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A classic Italian pasta dish...", result.get(0).getDescription());
        assertEquals("A spicy and flavorful dish...", result.get(1).getDescription());
    }

    @Test
    void testConstructRecipeDescription_Failure() throws Exception {
        // Given
        List<RecipeDTO> recipes = new ArrayList<>();
        RecipeDTO recipe1 = new RecipeDTO();
        recipe1.setName("Spaghetti Bolognese");
        recipes.add(recipe1);

        String responseBody = "{\"choices\":[{\"message\":{\"content\":\"Invalid JSON response\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Mocking ObjectMapper behavior
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Invalid JSON"));

        // When
        List<RecipeDTO> result = gptService.constructRecipeDescription(recipes);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getDescription());
    }

    @Test
    void testValidateFoodItems_Success() throws Exception {
        // Given
        List<String> words = Arrays.asList("tomato", "pizza", "laptop");
        String responseBody = "{\"choices\":[{\"message\":{\"content\":\"{\\\"tomato\\\": \\\"ingredient\\\", \\\"pizza\\\": \\\"dish\\\", \\\"laptop\\\": \\\"unknown\\\"}\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Mocking ObjectMapper behavior
        String jsonResponse = "{\"tomato\": \"ingredient\", \"pizza\": \"dish\", \"laptop\": \"unknown\"}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(jsonResponse);

        ObjectMapper realObjectMapper = new ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(invocation -> {
            String jsonInput = invocation.getArgument(0, String.class);

            if (jsonInput.equals(responseBody)) {
                // This is the first call - parsing full API response
                return realObjectMapper.readTree(responseBody);
            } else {
                // This is the second call - parsing GPT's returned JSON array
                return mockJsonNode;
            }
        });
        // When
        List<String> result = gptService.validateFoodItems(words);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("tomato"));
        assertTrue(result.contains("pizza"));
        assertFalse(result.contains("laptop"));
    }

    @Test
    void testValidateFoodItems_Failure() throws Exception {
        // Given
        List<String> words = Arrays.asList("tomato", "pizza", "laptop");
        String responseBody = "{\"choices\":[{\"message\":{\"content\":\"Invalid JSON response\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Mocking ObjectMapper behavior
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Invalid JSON"));

        // When
        List<String> result = gptService.validateFoodItems(words);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
