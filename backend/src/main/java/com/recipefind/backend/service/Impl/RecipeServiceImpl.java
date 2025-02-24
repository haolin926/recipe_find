package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.entity.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import com.recipefind.backend.service.RecipeService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final static List<String> wantedNutrition = List.of(
            "Calories",
            "Fat",
            "Carbohydrates",
            "Protein"
    );;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String apiKey = dotenv.get("SPOONACULAR_API_KEY");

    private static final String GOOGLE_VISION_API_KEY = dotenv.get("GOOGLE_VISION_API_KEY");

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY = 500; // 500 ms delay
    private static final String GOOGLE_VISION_URL = "https://vision.googleapis.com/v1/images:annotate?key=" + GOOGLE_VISION_API_KEY;
    public RecipeServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

    }
    private static final String SPOONACULAR_PARSE_INGREDIENT = "https://api.spoonacular.com/recipes/parseIngredients?apiKey=" + apiKey;


    @Override
    public List<RecipeDTO> findRecipesByName(String queryName) throws JsonProcessingException {
        String url = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + queryName + "&instructionsRequired=true&addRecipeInstructions=true&addRecipeNutrition=true&number=10";
        String response = restTemplate.getForEntity(url, String.class).getBody();
        JsonNode responseJson = objectMapper.readTree(response);
        List<RecipeDTO> recipeList = new ArrayList<>();
        JsonNode recipes = responseJson.path("results");
        for (JsonNode recipe : recipes) {
            RecipeDTO formattedRecipe = constructRecipe(recipe);
            System.out.println(formattedRecipe.getName());
            recipeList.add(formattedRecipe);
        }

        return recipeList;
    }



    @Override
    public RecipeDTO findRecipeById(Integer recipeId) throws JsonProcessingException {
        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=" + apiKey + "&includeNutrition=true";
        String response = restTemplate.getForEntity(url, String.class).getBody();
        JsonNode responseJson = objectMapper.readTree(response);

        return constructRecipe(responseJson);
    }

    @Override
    public RecipeDTO constructRecipe(JsonNode recipe) {

        RecipeDTO formattedRecipe = new RecipeDTO();

        JsonNode nutrientsNode = recipe.path("nutrition").path("nutrients");
        JsonNode ingredientsNode = recipe.path("nutrition").path("ingredients");
        JsonNode instructionsNode = recipe.path("analyzedInstructions");
        JsonNode stepsNode = instructionsNode.get(0).path("steps");

        String recipeName = recipe.path("title").asText();
        formattedRecipe.setName(recipeName);
        String image = recipe.path("image").asText();
        formattedRecipe.setImage(image);

        for (JsonNode nutrient : nutrientsNode) {
            String name = nutrient.path("name").asText();
            if (!wantedNutrition.contains(name)) {
                continue;
            }
            String amount = nutrient.path("amount").asText();
            String unit = nutrient.path("unit").asText();
            String amount_unit = amount + " " + unit;
            Nutrition nutrition = new Nutrition(name, amount);
            formattedRecipe.getNutrition().add(nutrition);
        }


        for (JsonNode ingredient : ingredientsNode) {
            String name = ingredient.path("name").asText();
            String amount = ingredient.path("amount").asText();
            String unit = ingredient.path("unit").asText();
            String amount_unit = amount + " " + unit;
            Ingredient formattedIngredient = new Ingredient(name,amount_unit);
            formattedRecipe.getIngredients().add(formattedIngredient);
        }

        for (JsonNode stepNode : stepsNode) {
            String step = stepNode.path("step").asText();
            formattedRecipe.getInstructions().add(step);
        }

        return formattedRecipe;
    }

    private List<Prediction> predictName(MultipartFile image) throws Exception {
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

            String modelUrl = "http://127.0.0.1:5000/predict";

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(modelUrl, requestEntity, String.class);
            String response = responseEntity.getBody();
            System.out.println("Response from model: " + response);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode predictedResults = jsonNode.path("predictions");

                List<Prediction> predictedNames = new ArrayList<>();
                for(JsonNode predictedResult : predictedResults) {
                    Prediction result = new Prediction();
                    String name = predictedResult.path("dish_name").asText().replace("_", " ");
                    Float probability = predictedResult.path("probability").floatValue();
                    result.setName(name);
                    result.setProbability(probability);

                    predictedNames.add(result);
                }

                return predictedNames;

            }
            else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    private List<String> labelIngredients (MultipartFile image) throws Exception {
        int retries = 0;
        try {
            // convert image to base64 for access the API
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            // construct request body
            String requestJson = "{ \"requests\": [ { \"image\": { \"content\": \"" + base64Image + "\" }, \"features\": [ { \"type\": \"LABEL_DETECTION\", \"maxResults\": 20 } ] } ] }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<String> detectedLabels = new ArrayList<>();

            while (retries < MAX_RETRIES) {
                try {
                    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_VISION_URL, entity, String.class);
                    System.out.println(response.getBody());

                    if (response.getStatusCode().is2xxSuccessful()) {

                        JsonNode rootNode = objectMapper.readTree(response.getBody());
                        System.out.println(rootNode.asText());
                        JsonNode labels = rootNode.path("responses").get(0).path("labelAnnotations");

                        for (JsonNode label : labels) {
                            String labelName = label.path("description").asText();
                            boolean isIngredient = isIngredient(labelName);
                            TimeUnit.MILLISECONDS.sleep(100);

                            if (isIngredient) {
                                detectedLabels.add(labelName);
                            }
                        }
                        return detectedLabels;
                    } else {
                        System.out.println("Error: Received status " + response.getStatusCode());
                        retries++;
                    }
                } catch (HttpClientErrorException.TooManyRequests e) {
                    retries++;
                    if (retries >= MAX_RETRIES) {
                        throw new Exception("Max retries reached", e);
                    }
                    System.out.println("Too many requests, retrying in 10 seconds...");
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY);
                } catch (Exception e) {
                    retries++; // Increment retries on unexpected errors
                    if (retries >= MAX_RETRIES) {
                        throw new Exception("Max retries reached due to unexpected error", e);
                    }
                    System.out.println("Unexpected error: " + e.getMessage() + ". Retrying in 5 seconds...");
                    TimeUnit.MILLISECONDS.sleep(5000); // Retry after 5 seconds
                }
            }
        } catch(Exception e){
            System.out.println("API request failed: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private boolean isIngredient (String name) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ingredientList", name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(SPOONACULAR_PARSE_INGREDIENT, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode ingredient = rootNode.get(0);
            return ingredient.has("id");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PredictResult imagePrediction (MultipartFile image) throws Exception {
        try {
            List<Prediction> predictName = predictName(image);

            List<String> ingredients = labelIngredients(image);
//            List<String> ingredients= new ArrayList<>();
//            ingredients.add("meal");
//            ingredients.add("banana");
//            ingredients.add("recipe");
            PredictResult predictResult = new PredictResult();

            predictResult.setPredictName(predictName);
            predictResult.setDetectedIngredients(ingredients);

            return predictResult;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
