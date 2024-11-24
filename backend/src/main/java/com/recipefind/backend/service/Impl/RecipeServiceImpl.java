package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.service.RecipeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String apiKey = "00ca39c7f34945afbe78f7288a036107";
    public RecipeServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

    }

    @Override
    public Recipe constructRecipe(String queryName) throws JsonProcessingException {
        String url = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + queryName + "&instructionsRequired=true&addRecipeInstructions=true&addRecipeNutrition=true&number=1";
        String response = restTemplate.getForEntity(url, String.class).getBody();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode results = jsonNode.path("results");

        Recipe recipe = new Recipe();

        List<String> wantedNutrients = new ArrayList<>();
        wantedNutrients.add("Calories");
        wantedNutrients.add("Fat");
        wantedNutrients.add("Carbohydrates");
        wantedNutrients.add("Protein");

        for (JsonNode result : results) {
            JsonNode nutrientsNode = result.path("nutrition").path("nutrients");
            JsonNode ingredientsNode = result.path("nutrition").path("ingredients");
            JsonNode instructionsNode = result.path("analyzedInstructions");
            JsonNode stepsNode = instructionsNode.get(0).path("steps");

            String recipeName = result.path("title").asText();
            recipe.setName(recipeName);
            String image = result.path("image").asText();
            recipe.setImage(image);

            for (JsonNode nutrient : nutrientsNode) {
                String title = nutrient.path("name").asText();
                if (!wantedNutrients.contains(title)) {
                    continue;
                }
                String amount = nutrient.path("amount").asText();
                String unit = nutrient.path("unit").asText();
                String amount_unit = amount + " " + unit;
                recipe.addNutrition(title, amount_unit);
            }


            for (JsonNode ingredient : ingredientsNode) {
                String name = ingredient.path("name").asText();
                String amount = ingredient.path("amount").asText();
                String unit = ingredient.path("unit").asText();
                String amount_unit = amount + " " + unit;

                recipe.addIngredient(name, amount_unit);
            }

            for (JsonNode stepNode : stepsNode) {
                String step = stepNode.path("step").asText();
                recipe.addInstruction(step);
            }
        }
        return recipe;
    }

    @Override
    public String predictName(MultipartFile image){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileAsResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", fileAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            System.out.println("Sending request to model with image" + image.getResource());
            String modelUrl = "http://127.0.0.1:5000/predict";

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(modelUrl, requestEntity, String.class);
            String response = responseEntity.getBody();
            System.out.println("Response from model: " + response);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return jsonNode.path("dish_name").asText().replace("_", " ");
            } else {
                return "Prediction failed";
            }
        } catch (Exception e) {
            return "No name found" + e.getMessage();
        }
    }
}
