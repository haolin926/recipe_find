package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.dao.CommentRepository;
import com.recipefind.backend.dao.RecipeRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.*;
import com.recipefind.backend.utils.FractionConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final SaveRecipeService saveRecipeService;
    private final IngredientService ingredientService;
    private final NutritionService nutritionService;
    private final FractionConverter fractionConverter;
    private final GptService gptService;
    private final static List<String> wantedNutrition = List.of(
            "Calories",
            "Fat",
            "Carbohydrates",
            "Protein"
    );
    private final CommentRepository commentRepository;
    @Setter
    @Value("${spoonacular.api.key}")
    private String apiKey;

    @Setter
    @Value("${google.vision.api.key}")
    private String googleVisionApiKey;

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY = 500; // 500 ms delay
    private static final String GOOGLE_VISION_URL = "https://vision.googleapis.com/v1/images:annotate?key=";
    private static final String SPOONACULAR_PARSE_INGREDIENT_URL = "https://api.spoonacular.com/recipes/parseIngredients?apiKey=";

    private static final String SPOONACULAR_FIND_RECIPE_BY_INGREDIENT_URL = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=";

    @Override
    public List<RecipeDTO> findRecipesByName(String queryName) throws JsonProcessingException {
        String url = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + queryName + "&instructionsRequired=true&addRecipeInstructions=true&addRecipeNutrition=true&number=10";
        String response = restTemplate.getForEntity(url, String.class).getBody();
        JsonNode responseJson = objectMapper.readTree(response);
        List<RecipeDTO> recipeList = new ArrayList<>();
        JsonNode recipes = responseJson.path("results");
        for (JsonNode recipe : recipes) {
            RecipeDTO formattedRecipe = constructRecipeFromComplexSearch(recipe);
            recipeList.add(formattedRecipe);
        }

        recipeList = gptService.constructRecipeDescription(recipeList);

        return recipeList;
    }

    @Override
    public RecipeDTO findRecipeInSpoonacularByApiId(Integer recipeApiId) throws JsonProcessingException {

        Recipe recipe = findRecipeByApiId(recipeApiId);
        if (recipe != null) {
            return recipe.convertToRecipeDTO();
        }

        String url = "https://api.spoonacular.com/recipes/" + recipeApiId + "/information?apiKey=" + apiKey + "&includeNutrition=true";
        String response = restTemplate.getForEntity(url, String.class).getBody();
        JsonNode responseJson = objectMapper.readTree(response);

        return constructRecipeFromInformation(responseJson);
    }

    @Override
    public Recipe findRecipeById(Integer recipeId) {
        return recipeRepository.findRecipeByRecipeId(recipeId.longValue());
    }


    private RecipeDTO constructRecipeFromComplexSearch(JsonNode recipe) {

        JsonNode nutrientsNode = recipe.path("nutrition").path("nutrients");
        JsonNode ingredientsNode = recipe.path("nutrition").path("ingredients");
        JsonNode instructionsNode = recipe.path("analyzedInstructions");
        JsonNode stepsNode = instructionsNode.get(0).path("steps");

        RecipeDTO formattedRecipe = constructBasicRecipeInformation(recipe);


        for (JsonNode nutrient : nutrientsNode) {
            String name = nutrient.path("name").asText();
            if (!wantedNutrition.contains(name)) {
                continue;
            }
            String amount = nutrient.path("amount").asText();
            String unit = nutrient.path("unit").asText();
            NutritionDTO nutritionDTO = new NutritionDTO(name, amount, unit);
            formattedRecipe.getNutritionDTOS().add(nutritionDTO);
        }


        for (JsonNode ingredient : ingredientsNode) {
            String name = ingredient.path("name").asText();
            String amount = ingredient.path("amount").asText();
            String unit = ingredient.path("unit").asText();
            int IngredientId = ingredient.path("id").asInt();
            IngredientDTO formattedIngredientDTO = new IngredientDTO(IngredientId, name, amount, unit);
            formattedRecipe.getIngredientDTOS().add(formattedIngredientDTO);
        }

        for (JsonNode stepNode : stepsNode) {
            String step = stepNode.path("step").asText();
            formattedRecipe.getInstructions().add(step);
        }

        return formattedRecipe;
    }

    private RecipeDTO constructRecipeFromInformation (JsonNode recipe) {
        RecipeDTO formattedRecipe = constructBasicRecipeInformation(recipe);

        //Get Ingredients
        JsonNode ingredientNode = recipe.path("extendedIngredients");

        List<IngredientDTO> ingredientDTOList = new ArrayList<>();
        for (JsonNode ingredient : ingredientNode) {
            int ingredientId = ingredient.path("id").asInt();
            String name = ingredient.path("name").asText();

            double amount = ingredient.path("amount").asDouble();
            String fractionAmount = fractionConverter.decimalToFraction(amount);
            String unit = ingredient.path("unit").asText();
            IngredientDTO ingredientDTO = new IngredientDTO(ingredientId, name, fractionAmount, unit);

            ingredientDTOList.add(ingredientDTO);
        }
        formattedRecipe.setIngredientDTOS(ingredientDTOList);

        // Get nutrition
        JsonNode nutrientsNode = recipe.path("nutrition").path("nutrients");

        List<NutritionDTO> nutritionDTOList = new ArrayList<>();

        if (nutrientsNode.isArray() && !nutrientsNode.isEmpty()) {
            for (JsonNode nutrient : nutrientsNode) {
                String name = nutrient.path("name").asText();
                if (!wantedNutrition.contains(name)) {
                    continue;
                }
                String amount = nutrient.path("amount").asText();
                String unit = nutrient.path("unit").asText();
                NutritionDTO nutritionDTO = new NutritionDTO(name, amount, unit);
                nutritionDTOList.add(nutritionDTO);
            }
        }
        formattedRecipe.setNutritionDTOS(nutritionDTOList);

        // Get Instruction
        JsonNode instructionsNode = recipe.path("analyzedInstructions");
        if (instructionsNode.isArray() && !instructionsNode.isEmpty()) {
            JsonNode stepsNode = instructionsNode.get(0).path("steps");
            for (JsonNode stepNode : stepsNode) {
                String step = stepNode.path("step").asText();
                formattedRecipe.getInstructions().add(step);
            }
        } else {
            List<String> instructions = new ArrayList<>();
            formattedRecipe.setInstructions(instructions);
        }

        formattedRecipe.setRate((float) 0);

        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        recipeDTOList.add(formattedRecipe);
        recipeDTOList = gptService.constructRecipeDescription(recipeDTOList);

        return recipeDTOList.get(0);
    }

    private RecipeDTO constructBasicRecipeInformation(JsonNode recipe) {
        RecipeDTO formattedRecipe = new RecipeDTO();
        String recipeName = recipe.path("title").asText();
        Integer id = recipe.path("id").asInt();
        String image = recipe.path("image").asText();
        boolean glutenFree = recipe.path("glutenFree").asBoolean();
        boolean dairyFree = recipe.path("dairyFree").asBoolean();
        boolean vegetarian = recipe.path("vegetarian").asBoolean();
        Integer cookTime = recipe.path("readyInMinutes").asInt();

        formattedRecipe.setRecipeApiId(id);
        formattedRecipe.setName(recipeName);
        formattedRecipe.setImage(image);
        formattedRecipe.setDairyFree(dairyFree);
        formattedRecipe.setGlutenFree(glutenFree);
        formattedRecipe.setVegetarian(vegetarian);
        formattedRecipe.setCookTime(cookTime);

        return formattedRecipe;
    }

    public RecipeDTO constructRecipeSearchByIngredient (JsonNode recipe) {
        RecipeDTO formattedRecipe = new RecipeDTO();
        String recipeName = recipe.path("title").asText();
        Integer id = recipe.path("id").asInt();
        String image = recipe.path("image").asText();
        List<String> usedIngredientNames = new ArrayList<>();

        JsonNode usedIngredients = recipe.path("usedIngredients");

        for (JsonNode usedIngredient : usedIngredients) {
            String name = usedIngredient.path("name").asText();
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            usedIngredientNames.add(capitalizedName);
        }

        formattedRecipe.setRecipeApiId(id);
        formattedRecipe.setName(recipeName);
        formattedRecipe.setImage(image);
        formattedRecipe.setUsedIngredients(usedIngredientNames);

        return formattedRecipe;
    }

    public List<Prediction> predictName(MultipartFile image) throws Exception {
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
                    float probability = predictedResult.path("probability").floatValue();

                    if(probability > 0) {
                        result.setName(name);
                        result.setProbability(probability);
                        predictedNames.add(result);
                    }
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


    public List<String> labelIngredients(MultipartFile image) throws Exception {
        int retries = 0;
        try {
            // convert image to base64 for access the API
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            // construct request body
            String requestJson = "{ \"requests\": [ { \"image\": { \"content\": \"" + base64Image + "\" }, \"features\": [ { \"type\": \"LABEL_DETECTION\", \"maxResults\": 20 } ] } ] }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<String> detectedLabels = new ArrayList<>();
            String url = getGoogleVisionUrl();

            while (retries < MAX_RETRIES) {
                try {
                    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {

                        JsonNode rootNode = objectMapper.readTree(response.getBody());

                        JsonNode labels = rootNode.path("responses").get(0).path("labelAnnotations");
                        if (!labels.isArray() || labels.isEmpty()) {
                            System.err.println("no labels detected");
                            return null;
                        }
                        for (JsonNode label : labels) {
                            double score = label.path("score").asDouble(0.0);

                            if (score > 0.5) {

                                String labelName = label.path("description").asText();
                                boolean isIngredient = isIngredient(labelName);
                                TimeUnit.MILLISECONDS.sleep(100); // Sleep for 100 ms to avoid rate limiting

                                if (isIngredient) {
                                    detectedLabels.add(labelName);
                                }
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
            throw new Exception("API request failed: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public boolean isIngredient (String name) {
        String url = getCheckIngredientURL();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ingredientList", name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

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
            List<String> filteredIngredients = gptService.validateFoodItems(ingredients);
            PredictResult predictResult = new PredictResult();
            predictResult.setPredictName(predictName);
            predictResult.setDetectedIngredients(filteredIngredients);

            return predictResult;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    @Transactional
    public Integer saveFavouriteRecipe(RecipeDTO recipeDTO, Integer userId) {

        //check if this recipe already in the db
        Recipe existedRecipe = findRecipeByApiId(recipeDTO.getRecipeApiId());
        if(existedRecipe != null) {
            System.out.println("Find matching recipe, saving into db");
            return saveRecipeService.saveRecipeForUser(userId, existedRecipe);
        }
        else {
            Recipe savedRecipe = saveRecipe(recipeDTO);

            if (savedRecipe != null) {
                System.out.println("Saved recipe into db");
                return saveRecipeService.saveRecipeForUser(userId, savedRecipe);

            } else {
                return null;
            }
        }
    }

    @Override
    @Transactional
    public Recipe saveRecipe(RecipeDTO recipeDTO) {

        Recipe recipe = new Recipe();
        recipe.setRecipeApiId(recipeDTO.getRecipeApiId());
        recipe.setName(recipeDTO.getName());
        recipe.setImageUrl(recipeDTO.getImage());
        recipe.setInstruction(recipeDTO.getInstructions());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setDairyFree(recipeDTO.isDairyFree());
        recipe.setGlutenFree(recipeDTO.isGlutenFree());
        recipe.setVegetarian(recipeDTO.isVegetarian());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setRate(recipeDTO.getRate());

        List<RecipeIngredientsEntity> recipeIngredientsEntities = new ArrayList<>();
        try {
            for (IngredientDTO ingredientDTO : recipeDTO.getIngredientDTOS()) {
                RecipeIngredientsEntity recipeIngredientsEntity = new RecipeIngredientsEntity();

                IngredientsEntity ingredientsEntity = ingredientService.findOrSaveIngredient(ingredientDTO.getName());
                BigDecimal ingredientAmount = fractionConverter.fractionToDecimal(ingredientDTO.getAmount());

                recipeIngredientsEntity.setIngredientsEntity(ingredientsEntity);
                recipeIngredientsEntity.setIngredientAmount(ingredientAmount);
                recipeIngredientsEntity.setIngredientUnit(ingredientDTO.getUnit());
                recipeIngredientsEntity.setRecipe(recipe);

                recipeIngredientsEntities.add(recipeIngredientsEntity);
            }
            recipe.setRecipeIngredientsEntities(recipeIngredientsEntities);

            List<RecipeNutritionEntity> recipeNutritionEntities = new ArrayList<>();
            for (NutritionDTO nutritionDTO : recipeDTO.getNutritionDTOS()) {
                NutritionEntity nutritionEntity = nutritionService.findOrSaveNutrition(nutritionDTO.getName());

                RecipeNutritionEntity recipeNutritionEntity = new RecipeNutritionEntity();

                recipeNutritionEntity.setNutritionEntity(nutritionEntity);
                recipeNutritionEntity.setAmount(new BigDecimal(nutritionDTO.getAmount()));
                recipeNutritionEntity.setUnit(nutritionDTO.getUnit());
                recipeNutritionEntity.setRecipe(recipe);

                recipeNutritionEntities.add(recipeNutritionEntity);
            }

            recipe.setRecipeNutritionEntities(recipeNutritionEntities);
            System.out.println("construct recipe success");

            return recipeRepository.save(recipe);

        } catch (Exception e) {
            System.err.println("Error saving recipe: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<RecipeDTO> findRecipesByIngredients(List<String> ingredients) throws JsonProcessingException {
        String ingredientsParam = String.join(",", ingredients);
        String url = getSpoonacularFindRecipeByIngredientUrl() + "&ingredients=" + ingredientsParam;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            List<RecipeDTO> recipeDTOList = new ArrayList<>();
            for (JsonNode recipe : rootNode) {

                RecipeDTO recipeDTO = constructRecipeSearchByIngredient(recipe);

                recipeDTOList.add(recipeDTO);
            }
            recipeDTOList = gptService.constructRecipeDescription(recipeDTOList);

            return recipeDTOList;
        }
        return null;
    }

    @Override
    public Recipe findRecipeByApiId(Integer recipeApiId) {
        return recipeRepository.findRecipeByRecipeApiId(recipeApiId);
    }

    @Override
    public Boolean updateRecipeRate(Long recipeId, Float rate) {
        try {
            Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId);
            Float currentRate = recipe.getRate();
            Integer totalCommentNumber = commentRepository.countCommentsByRecipeIdAndRateNot(recipeId.intValue(), (float) 0);
            Float newRate = (currentRate + rate) / (totalCommentNumber);

            recipe.setRate(newRate);
            recipeRepository.save(recipe);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getGoogleVisionUrl() {
        return GOOGLE_VISION_URL + googleVisionApiKey;
    }

    private String getCheckIngredientURL() {
        return SPOONACULAR_PARSE_INGREDIENT_URL + apiKey;
    }

    private String getSpoonacularFindRecipeByIngredientUrl() {
        return SPOONACULAR_FIND_RECIPE_BY_INGREDIENT_URL + apiKey;
    }

}
