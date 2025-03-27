package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.GptService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptServiceImpl implements GptService {
    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(GptServiceImpl.class);

    @Override
    public List<RecipeDTO> constructRecipeDescription(List<RecipeDTO> recipes) {
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "You are a professional chef. Generate a short, engaging description (under 100 words) for each recipe provided. " +
                        "Return the result as a **valid JSON array** in the following format: " +
                        "[{\"recipe\": \"Recipe Name\", \"description\": \"Generated description...\"}]. " +
                        "Ensure the response contains **only JSON** with no extra text, no explanations, and no markdown formatting."
        ));

        StringBuilder userPrompt = new StringBuilder("Here are the recipes:\n");
        for (RecipeDTO recipe : recipes) {
            userPrompt.append("- ").append(recipe.getName()).append("\n");
        }

        messages.add(Map.of("role", "user", "content", userPrompt.toString()));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", Math.min(500, 150 * recipes.size())); // Adjust token limit for batch

        // Create the request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send request to OpenAI
        ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, requestEntity, String.class);

        // Parse JSON response safely
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode choicesNode = rootNode.path("choices");

            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                String responseText = choicesNode.get(0).path("message").path("content").asText().trim();

                responseText = responseText.replaceAll("```json", "").replaceAll("```", "").trim();

                if (!responseText.startsWith("[") || !responseText.endsWith("]")) {
                    System.err.println("Invalid JSON format from OpenAI: " + responseText);
                    return recipes;
                }

                // Parse the JSON list returned by GPT
                JsonNode jsonDescriptions = objectMapper.readTree(responseText);

                if (jsonDescriptions.isArray()) {
                    for (JsonNode jsonRecipe : jsonDescriptions) {
                        String recipeName = jsonRecipe.path("recipe").asText();
                        String description = jsonRecipe.path("description").asText();

                        // Find matching RecipeDTO and update it
                        for (RecipeDTO recipe : recipes) {
                            if (recipe.getName().equalsIgnoreCase(recipeName)) {
                                recipe.setDescription(description);
                                break;
                            }
                        }
                    }
                }
            }
            return recipes;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return recipes;
        }
    }

    @Override
    public List<String> validateFoodItems(List<String> words) {
        // Define HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "You are a food expert. Classify each word as 'ingredient', 'dish', or 'unknown'. " +
                        "Return a JSON object where each word maps to its category."));

        // Construct the user query
        messages.add(Map.of("role", "user", "content", "Classify the following words: " + String.join(", ", words)));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 200); // Limit response length

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, requestEntity, String.class);

        List<String> validatedItems = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode choicesNode = rootNode.path("choices");

            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                String responseText = choicesNode.get(0).path("message").path("content").asText().trim();

                // Parse the JSON response (Example: {"tomato": "ingredient", "pizza": "dish", "laptop": "unknown"})
                JsonNode classificationNode = objectMapper.readTree(responseText);

                for (String word : words) {
                    String category = classificationNode.path(word).asText();
                    if ("ingredient".equalsIgnoreCase(category) || "dish".equalsIgnoreCase(category)) {
                        validatedItems.add(word);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return validatedItems;
    }


}
