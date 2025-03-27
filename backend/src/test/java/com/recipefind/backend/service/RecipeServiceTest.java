package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.dao.CommentRepository;
import com.recipefind.backend.dao.RecipeRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.Impl.RecipeServiceImpl;
import com.recipefind.backend.utils.FractionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {
    @InjectMocks
    @Spy
    private RecipeServiceImpl recipeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private SaveRecipeService saveRecipeService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private NutritionService nutritionService;

    @Mock
    private FractionConverter fractionConverter;

    @Mock
    private GptService gptService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MultipartFile image;

    private static final String FAKE_GOOGLE_VISION_URL = "https://vision.googleapis.com/v1/images:annotate?key=mockApiKey";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeService.setApiKey("mockApiKey");
        recipeService.setGoogleVisionApiKey("mockApiKey");
    }

    private String readSampleResponse(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        Path path = resource.getFile().toPath();
        return Files.readString(path);
    }

    private Recipe createMockRecipe() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);
        recipe.setRecipeApiId(123);
        recipe.setName("Test Recipe");
        recipe.setImageUrl("https://example.com/image.jpg");
        recipe.setDescription("A delicious test recipe.");
        recipe.setInstruction(Arrays.asList("Step 1", "Step 2"));
        recipe.setDairyFree(true);
        recipe.setGlutenFree(false);
        recipe.setVegetarian(true);
        recipe.setCookTime(30);
        recipe.setRate(4.5f);

        IngredientsEntity ingredients = new IngredientsEntity();
        ingredients.setIngredientId(1L);
        ingredients.setIngredientName("Flour");

        RecipeIngredientsEntity ingredientEntity = new RecipeIngredientsEntity();
        ingredientEntity.setIngredientsEntity(ingredients);
        ingredientEntity.setIngredientAmount(BigDecimal.valueOf(2.5));
        ingredientEntity.setIngredientUnit("cups");
        recipe.setRecipeIngredientsEntities(List.of(ingredientEntity));

        NutritionEntity nutrition = new NutritionEntity();
        nutrition.setNutritionId(1);
        nutrition.setNutritionName("Calories");

        RecipeNutritionEntity nutritionEntity = new RecipeNutritionEntity();
        nutritionEntity.setNutritionEntity(nutrition);
        nutritionEntity.setAmount(BigDecimal.valueOf(250.0));
        recipe.setRecipeNutritionEntities(List.of(nutritionEntity));

        return recipe;
    }

    private RecipeDTO createTestRecipeDTO() {
        RecipeDTO recipe = new RecipeDTO();
        recipe.setRecipeApiId(635675);
        recipe.setName("Boozy Bbq Chicken");
        recipe.setImage("https://img.spoonacular.com/recipes/635675-312x231.jpg");
        recipe.setInstructions(List.of(
                "Cut orange, peppers, onion & broccoli into large bite-sized chunks (at least 1\" thick) and place in the dish.",
                "Add mushrooms and tomatoes. Stir veggies in marinade to coat. Cover and refrigerate while you prep chicken.",
                "Warm outdoor grill to medium heat."
        ));

        recipe.setIngredientDTOS(List.of(
                new IngredientDTO(14106, "white wine", "0.04", "cup"),
                new IngredientDTO(14003, "beer", "2.0", "ounces"),
                new IngredientDTO(11090, "broccoli", "0.17", "head"),
                new IngredientDTO(5006, "chicken", "0.5", "lbs")
        ));

        recipe.setNutritionDTOS(List.of(
                new NutritionDTO("Calories", "725.28", "kcal"),
                new NutritionDTO("Fat", "32.78", "g"),
                new NutritionDTO("Carbohydrates", "72.28", "g"),
                new NutritionDTO("Protein", "32.27", "g")
        ));

        recipe.setDairyFree(true);
        recipe.setGlutenFree(true);
        recipe.setVegetarian(false);
        recipe.setCookTime(45);

        return recipe;
    }

    @Test
    void testFindRecipesByName() throws Exception {
        // Arrange
        String queryName = "pasta";
        String apiResponse = readSampleResponse("sampleFindByName.json");
        JsonNode mockJsonNode = new ObjectMapper().readTree(apiResponse);
        List<RecipeDTO> mockRecipeList = new ArrayList<>();
        RecipeDTO mockRecipe = createTestRecipeDTO();
        mockRecipeList.add(mockRecipe);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(new org.springframework.http.ResponseEntity<>(apiResponse, org.springframework.http.HttpStatus.OK));
        when(objectMapper.readTree(apiResponse)).thenReturn(mockJsonNode);
        when(gptService.constructRecipeDescription(anyList())).thenReturn(mockRecipeList);

        // Act
        List<RecipeDTO> result = recipeService.findRecipesByName(queryName);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Boozy Bbq Chicken", result.get(0).getName());
        assertEquals("https://img.spoonacular.com/recipes/635675-312x231.jpg", result.get(0).getImage());
        assertEquals(635675, result.get(0).getRecipeApiId());

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
        verify(objectMapper, times(1)).readTree(apiResponse);
        verify(gptService, times(1)).constructRecipeDescription(anyList());
    }

    @Test
    void testFindRecipeInSpoonacularByApiId_WhenRecipeExistsInDb() throws JsonProcessingException {
        Integer recipeApiId = 123;
        Recipe recipe = createMockRecipe();
        RecipeDTO recipeDTO = new RecipeDTO();

        Recipe recipeSpy = spy(recipe);
        when(recipeSpy.convertToRecipeDTO()).thenReturn(recipeDTO);

        when(recipeRepository.findRecipeByRecipeApiId(recipeApiId)).thenReturn(recipeSpy);

        RecipeDTO result = recipeService.findRecipeInSpoonacularByApiId(recipeApiId);

        assertNotNull(result);
        assertEquals(recipeDTO, result);
        verify(recipeRepository, times(1)).findRecipeByRecipeApiId(recipeApiId);
        verifyNoInteractions(restTemplate, objectMapper);
    }

    @Test
    void testFindRecipeInSpoonacularByApiId_WhenRecipeNotInDB() throws IOException {
        // Given
        int recipeApiId = 123;
        String apiKey = "mockApiKey";
        String url = "https://api.spoonacular.com/recipes/" + recipeApiId + "/information?apiKey=" + apiKey + "&includeNutrition=true";
        String jsonResponse = readSampleResponse("sampleFindById.json");
        JsonNode mockJsonNode = new ObjectMapper().readTree(jsonResponse);

        RecipeDTO mockRecipeDTO = createTestRecipeDTO();

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        recipeDTOList.add(mockRecipeDTO);

        when(recipeService.findRecipeByApiId(recipeApiId)).thenReturn(null);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(mockResponseEntity);
        when(objectMapper.readTree(jsonResponse)).thenReturn(mockJsonNode);
        when(gptService.constructRecipeDescription(anyList())).thenReturn(recipeDTOList);
        // When
        RecipeDTO result = recipeService.findRecipeInSpoonacularByApiId(recipeApiId);

        // Then
        assertNotNull(result);
        assertEquals("Boozy Bbq Chicken", result.getName());

        // Verify interactions
        verify(restTemplate, times(1)).getForEntity(url, String.class);
        verify(objectMapper, times(1)).readTree(jsonResponse);
        verify(gptService, times(1)).constructRecipeDescription(anyList());
    }

    @Test
    void testImagePrediction() throws Exception {
        // Mocking image data
        byte[] imageBytes = "fake image data".getBytes();
        when(image.getBytes()).thenReturn(imageBytes);
        when(image.getOriginalFilename()).thenReturn("test.jpg");

        // Mocking predictName response
        List<Prediction> mockPredictions = new ArrayList<>();
        Prediction prediction = new Prediction();
        prediction.setName("Spaghetti");
        prediction.setProbability(0.9f);
        mockPredictions.add(prediction);

        doReturn(mockPredictions).when(recipeService).predictName(image);

        // Mocking labelIngredients response
        List<String> mockIngredients = Arrays.asList("Tomato", "Garlic", "Pasta");
        doReturn(mockIngredients).when(recipeService).labelIngredients(image);

        // Mocking GPT validation
        List<String> validatedIngredients = Arrays.asList("Tomato", "Garlic");
        when(gptService.validateFoodItems(mockIngredients)).thenReturn(validatedIngredients);

        // Call the method
        PredictResult result = recipeService.imagePrediction(image);

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.getPredictName().size());
        assertEquals("Spaghetti", result.getPredictName().get(0).getName());
        assertEquals(0.9f, result.getPredictName().get(0).getProbability(), 0.001);
        assertEquals(2, result.getDetectedIngredients().size());
        assertTrue(result.getDetectedIngredients().contains("Tomato"));
        assertTrue(result.getDetectedIngredients().contains("Garlic"));

        // Verify interactions
        verify(recipeService).predictName(image);
        verify(recipeService).labelIngredients(image);
        verify(gptService).validateFoodItems(mockIngredients);
    }

    @Test
    void testFindRecipeById() {
        // Arrange
        Integer recipeId = 1;
        Recipe mockRecipe = new Recipe();
        mockRecipe.setRecipeId(1L);
        mockRecipe.setName("Test Recipe");

        when(recipeRepository.findRecipeByRecipeId(recipeId.longValue())).thenReturn(mockRecipe);

        // Act
        Recipe result = recipeService.findRecipeById(recipeId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getRecipeId());
        assertEquals("Test Recipe", result.getName());

        verify(recipeRepository, times(1)).findRecipeByRecipeId(recipeId.longValue());
    }

    @Test
    void testPredictName() throws Exception {
        // Setup mock data
        byte[] imageBytes = "fake image data".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);

        // Mock response from the model
        String jsonResponse = "{ \"predictions\": [ { \"dish_name\": \"Spaghetti\", \"probability\": 0.9 }, { \"dish_name\": \"Pizza\", \"probability\": 0.8 } ] }";

        // Mock the RestTemplate postForEntity method
        ResponseEntity<String> mockResponse = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        // Mock the ObjectMapper to return the JsonNode
        JsonNode mockJsonNode = mock(JsonNode.class);
        JsonNode mockPredictions = mock(JsonNode.class);

        // Stubbing ObjectMapper to return mockJsonNode when readTree is called
        when(objectMapper.readTree(jsonResponse)).thenReturn(mockJsonNode);

        // Stubbing mockJsonNode to return mockPredictions when path("predictions") is called
        when(mockJsonNode.path("predictions")).thenReturn(mockPredictions);

        // Creating mock prediction results
        JsonNode prediction1 = mockPrediction("Spaghetti", 0.9f);
        JsonNode prediction2 = mockPrediction("Pizza", 0.8f);

        // Stubbing iterator to return mocked predictions
        when(mockPredictions.iterator()).thenReturn(List.of(prediction1, prediction2).iterator());

        // Call the method under test
        List<Prediction> predictions = recipeService.predictName(image);

        // Verify the results
        assertNotNull(predictions);
        assertEquals(2, predictions.size());
        assertEquals("Spaghetti", predictions.get(0).getName());
        assertEquals(0.9f, predictions.get(0).getProbability(), 0.001);
        assertEquals("Pizza", predictions.get(1).getName());
        assertEquals(0.8f, predictions.get(1).getProbability(), 0.001);

        // Verify interactions with mocks
        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
        verify(objectMapper).readTree(jsonResponse);
    }

    // Helper method to mock a Prediction object
    private JsonNode mockPrediction(String name, float probability) {
        JsonNode predictionNode = mock(JsonNode.class);
        JsonNode dishNameNode = mockTextNode(name);
        JsonNode probabilityNode = mockFloatNode(probability);

        when(predictionNode.path("dish_name")).thenReturn(dishNameNode);
        when(predictionNode.path("probability")).thenReturn(probabilityNode);

        return predictionNode;
    }

    // Helper method to mock a JsonNode for a string
    private JsonNode mockTextNode(String text) {
        JsonNode textNode = mock(JsonNode.class);
        when(textNode.asText()).thenReturn(text);
        return textNode;
    }

    // Helper method to mock a JsonNode for a float
    private JsonNode mockFloatNode(float value) {
        JsonNode floatNode = mock(JsonNode.class);
        when(floatNode.floatValue()).thenReturn(value);
        return floatNode;
    }

    @Test
    void testLabelIngredients_withValidImage() throws Exception {
        // Mocking the image data
        byte[] imageBytes = "fake image data".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);

        // Mock the response from Google Vision API (mocking the REST call)
        String googleVisionResponse = "{\n" +
                "  \"responses\": [\n" +
                "    {\n" +
                "      \"labelAnnotations\": [\n" +
                "        { \"description\": \"Tomato\", \"score\": 0.8 },\n" +
                "        { \"description\": \"Carrot\", \"score\": 0.6 },\n" +
                "        { \"description\": \"Computer\", \"score\": 0.1 }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JsonNode responseNode = new ObjectMapper().readTree(googleVisionResponse);

        ResponseEntity<String> mockGoogleVisionResponse = new ResponseEntity<>(googleVisionResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(FAKE_GOOGLE_VISION_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockGoogleVisionResponse);
        when(objectMapper.readTree(googleVisionResponse)).thenReturn(responseNode);

        // Mocking the behavior of isIngredient method to only return true for "Tomato" and "Carrot"
        doReturn(true).when(recipeService).isIngredient("Tomato");
        doReturn(true).when(recipeService).isIngredient("Carrot");
        doReturn(false).when(recipeService).isIngredient("Computer");

        // Call the method under test
        List<String> ingredients = recipeService.labelIngredients(image);

        // Assert that the correct ingredients are detected
        assertNotNull(ingredients);
        assertEquals(2, ingredients.size());
        assertTrue(ingredients.contains("Tomato"));
        assertTrue(ingredients.contains("Carrot"));
        assertFalse(ingredients.contains("Computer"));
    }

    @Test
    void testLabelIngredients_withNoValidLabels() throws Exception {
        // Mocking the image data
        byte[] imageBytes = "fake image data".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);

        // Mock the response from Google Vision API (mocking the REST call)
        String googleVisionResponse = "{\n" +
                "  \"responses\": [\n" +
                "    {\n" +
                "      \"labelAnnotations\": [\n" +
                "        { \"description\": \"Computer\", \"score\": 0.1 },\n" +
                "        { \"description\": \"Phone\", \"score\": 0.2 }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JsonNode responseNode = new ObjectMapper().readTree(googleVisionResponse);

        ResponseEntity<String> mockGoogleVisionResponse = new ResponseEntity<>(googleVisionResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(FAKE_GOOGLE_VISION_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockGoogleVisionResponse);
        when(objectMapper.readTree(googleVisionResponse)).thenReturn(responseNode);

        // Mocking the behavior of isIngredient method to return false for all labels
        doReturn(false).when(recipeService).isIngredient("Computer");
        doReturn(false).when(recipeService).isIngredient("Phone");

        // Call the method under test
        List<String> ingredients = recipeService.labelIngredients(image);

        // Assert that no ingredients are detected
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void testLabelIngredients_withRetriesAndErrorHandling() throws Exception {
        // Mocking the image data
        byte[] imageBytes = "fake image data".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);

        // Simulating API failure (for retries)
        when(restTemplate.postForEntity(eq(FAKE_GOOGLE_VISION_URL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        // Retry scenario should throw an exception after MAX_RETRIES are exhausted
        assertThrows(Exception.class, () -> recipeService.labelIngredients(image));
    }

    @Test
    void testSaveFavouriteRecipe_existingRecipe() {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);
        recipeDTO.setName("Test Recipe");
        recipeDTO.setImage("test_image.jpg");
        Integer userId = 1;

        Recipe recipe = new Recipe();
        recipe.setRecipeApiId(1);
        recipe.setName("Test Recipe");
        recipe.setImageUrl("test_image.jpg");

        when(recipeRepository.findRecipeByRecipeApiId(1)).thenReturn(recipe);
        when(saveRecipeService.saveRecipeForUser(userId, recipe)).thenReturn(1);

        Integer result = recipeService.saveFavouriteRecipe(recipeDTO, userId);

        assertNotNull(result);
        assertEquals(1, result);
        verify(recipeRepository).findRecipeByRecipeApiId(1);
        verify(saveRecipeService).saveRecipeForUser(userId, recipe);
    }

    @Test
    void testSaveFavouriteRecipe_newRecipe() {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);
        recipeDTO.setName("Test Recipe");
        recipeDTO.setImage("test_image.jpg");
        Integer userId = 1;

        Recipe recipe = new Recipe();
        recipe.setRecipeApiId(1);
        recipe.setName("Test Recipe");
        recipe.setImageUrl("test_image.jpg");

        when(recipeService.findRecipeByApiId(1)).thenReturn(null);
        when(recipeService.saveRecipe(recipeDTO)).thenReturn(recipe);
        when(saveRecipeService.saveRecipeForUser(userId, recipe)).thenReturn(1);

        // Call the method under test
        Integer result = recipeService.saveFavouriteRecipe(recipeDTO, userId);

        // Assert the results
        assertNotNull(result);
        assertEquals(1, result);

        // Verify interactions with mocks
        verify(recipeRepository).findRecipeByRecipeApiId(1);
        verify(recipeService).saveRecipe(recipeDTO);
        verify(saveRecipeService).saveRecipeForUser(userId, recipe);
    }

    @Test
    void testSaveFavouriteRecipe_newRecipe_saveFails() {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);
        recipeDTO.setName("Test Recipe");
        recipeDTO.setImage("test_image.jpg");
        Integer userId = 1;

        Recipe recipe = new Recipe();
        recipe.setRecipeApiId(1);
        recipe.setName("Test Recipe");
        recipe.setImageUrl("test_image.jpg");

        when(recipeService.findRecipeByApiId(1)).thenReturn(null);
        when(recipeService.saveRecipe(recipeDTO)).thenReturn(null);

        // Call the method under test
        Integer result = recipeService.saveFavouriteRecipe(recipeDTO, userId);

        // Assert the results
        assertNull(result);

        // Verify interactions with mocks
        verify(recipeRepository).findRecipeByRecipeApiId(1);
        verify(recipeService).saveRecipe(recipeDTO);
        verify(saveRecipeService, never()).saveRecipeForUser(anyInt(), any(Recipe.class)); // Never called since saveRecipe returned null
    }

    @Test
    void testSaveRecipe() {
        // Prepare input data
        Integer recipeApiId = 1;
        String name = "Spaghetti";
        String image = "http://image.com/spaghetti.jpg";
        String instruction = "Boil water and cook pasta.";
        List<String> instructions = new ArrayList<>();
        instructions.add(instruction);
        String description = "Delicious spaghetti recipe";
        boolean dairyFree = true;
        boolean glutenFree = true;
        boolean vegetarian = true;
        int cookTime = 30;
        double rate = 4.5;

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("testIngredient");
        ingredientDTO.setUnit("testUnit");

        List<IngredientDTO> ingredientDTOList = new ArrayList<>();
        ingredientDTOList.add(ingredientDTO);

        NutritionDTO nutritionDTO = new NutritionDTO();
        nutritionDTO.setName("testNutrition");
        nutritionDTO.setAmount("1");
        nutritionDTO.setUnit("testUnit");

        List<NutritionDTO> nutritionDTOList = new ArrayList<>();
        nutritionDTOList.add(nutritionDTO);

        NutritionEntity nutritionEntity = new NutritionEntity();
        IngredientsEntity ingredientsEntity = new IngredientsEntity();

        Recipe recipe = new Recipe();
        recipe.setRecipeApiId(recipeApiId);
        recipe.setName(name);
        recipe.setImageUrl(image);
        recipe.setInstruction(instructions);
        recipe.setDescription(description);
        recipe.setDairyFree(dairyFree);
        recipe.setGlutenFree(glutenFree);
        recipe.setVegetarian(vegetarian);
        recipe.setCookTime(cookTime);
        recipe.setRate((float) rate);

        RecipeDTO recipeDTO = new RecipeDTO();

        recipeDTO.setRecipeApiId(recipeApiId);
        recipeDTO.setName(name);
        recipeDTO.setImage(image);
        recipeDTO.setInstructions(instructions);
        recipeDTO.setDescription(description);
        recipeDTO.setDairyFree(dairyFree);
        recipeDTO.setGlutenFree(glutenFree);
        recipeDTO.setVegetarian(vegetarian);
        recipeDTO.setCookTime(cookTime);
        recipeDTO.setRate((float) rate);
        recipeDTO.setIngredientDTOS(ingredientDTOList);
        recipeDTO.setNutritionDTOS(nutritionDTOList);

        // Mock behavior of the ingredient and nutrition services
        when(ingredientService.findOrSaveIngredient(ingredientDTO.getName())).thenReturn(ingredientsEntity);
        when(fractionConverter.fractionToDecimal(ingredientDTO.getAmount())).thenReturn(new BigDecimal("1.0"));

        when(nutritionService.findOrSaveNutrition(nutritionDTO.getName())).thenReturn(nutritionEntity);

        // Mock behavior of Recipe repository
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        // Call the method under test
        Recipe savedRecipe = recipeService.saveRecipe(recipeDTO);

        // Verify the interactions with the mocks
        verify(ingredientService).findOrSaveIngredient(ingredientDTO.getName());
        verify(fractionConverter).fractionToDecimal(ingredientDTO.getAmount());
        verify(nutritionService).findOrSaveNutrition(nutritionDTO.getName());
        verify(recipeRepository).save(any(Recipe.class));

        // Assert the result
        assertNotNull(savedRecipe);
        assertEquals(recipeApiId, savedRecipe.getRecipeApiId());
        assertEquals(name, savedRecipe.getName());
        assertEquals(image, savedRecipe.getImageUrl());
        assertEquals(instructions, savedRecipe.getInstruction());
        assertEquals(description, savedRecipe.getDescription());
        assertEquals(dairyFree, savedRecipe.isDairyFree());
        assertEquals(glutenFree, savedRecipe.isGlutenFree());
        assertEquals(vegetarian, savedRecipe.isVegetarian());
        assertEquals(cookTime, savedRecipe.getCookTime());
        assertEquals(rate, savedRecipe.getRate(), 0.01);
    }

    @Test
    void testSaveRecipe_withException() {
        // Prepare mock data
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("test recipe");

        // Simulate error: Throw an exception when trying to save ingredient
        when(recipeRepository.save(any(Recipe.class))).thenThrow(new RuntimeException("Error"));

        // Call the method under test and handle the exception
        Recipe savedRecipe = recipeService.saveRecipe(recipeDTO);

        // Assert that the result is null because saving failed
        assertNull(savedRecipe);
    }

    @Test
    void testFindRecipesByIngredients() throws Exception {
        // Step 1: Prepare the test data
        List<String> ingredients = Arrays.asList("tomato", "onion", "garlic");

        // Prepare the mock response body (this would be the JSON response from the Spoonacular API)
        String jsonResponse = "[{" +
                "\"id\": 1, " +
                "\"title\": \"Tomato Soup\", " +
                "\"image\": \"http://example.com/image1.jpg\"}, " +
                "{" +
                "\"id\": 2, " +
                "\"title\": \"Garlic Bread\", " +
                "\"image\": \"http://example.com/image2.jpg\"}]";

        // Mock the restTemplate.getForEntity method to return a mocked response
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        // Mock the ObjectMapper to parse the mock response
        JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);

        // Mock the gptService.constructRecipeDescription to return the mock list of RecipeDTOs
        RecipeDTO recipeDTO1 = new RecipeDTO();
        recipeDTO1.setName("Tomato Soup");
        RecipeDTO recipeDTO2 = new RecipeDTO();
        recipeDTO2.setName("Garlic Bread");
        List<RecipeDTO> mockedRecipeDTOList = Arrays.asList(recipeDTO1, recipeDTO2);
        when(gptService.constructRecipeDescription(anyList())).thenReturn(mockedRecipeDTOList);

        // Step 2: Call the method under test
        List<RecipeDTO> result = recipeService.findRecipesByIngredients(ingredients);

        // Step 3: Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tomato Soup", result.get(0).getName());
        assertEquals("Garlic Bread", result.get(1).getName());

        // Verify interactions with mocks
        verify(restTemplate).getForEntity(anyString(), eq(String.class));
        verify(gptService).constructRecipeDescription(anyList());
    }

    @Test
    void testConstructRecipeSearchByIngredient() {
        // Step 1: Prepare the mock JsonNode to simulate the response from the Spoonacular API
        JsonNode mockJsonNode = mock(JsonNode.class);

        // Mock the main recipe properties
        when(mockJsonNode.path("id")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.path("title")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.path("image")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.path("usedIngredients")).thenReturn(mock(JsonNode.class));

        // Mock values for id, title, image
        when(mockJsonNode.path("id").asInt()).thenReturn(1);
        when(mockJsonNode.path("title").asText()).thenReturn("Tomato Soup");
        when(mockJsonNode.path("image").asText()).thenReturn("http://example.com/image.jpg");

        // Mock used ingredients array
        JsonNode usedIngredientsNode = mock(JsonNode.class);
        when(mockJsonNode.path("usedIngredients")).thenReturn(usedIngredientsNode);

        // Mock isArray() and size() for used ingredients
        when(usedIngredientsNode.isArray()).thenReturn(true);
        when(usedIngredientsNode.size()).thenReturn(3);

        // Mock individual ingredients
        JsonNode ingredient1 = mock(JsonNode.class);
        JsonNode ingredient2 = mock(JsonNode.class);
        JsonNode ingredient3 = mock(JsonNode.class);
        when(usedIngredientsNode.get(0)).thenReturn(ingredient1);
        when(usedIngredientsNode.get(1)).thenReturn(ingredient2);
        when(usedIngredientsNode.get(2)).thenReturn(ingredient3);

        // Create a list of mocked ingredients
        List<JsonNode> ingredientsList = List.of(ingredient1, ingredient2, ingredient3);

        // Mock the iterator() method to return an iterator over the list of ingredients
        when(usedIngredientsNode.iterator()).thenReturn(ingredientsList.iterator());

        // Mock ingredient names
        JsonNode ingredient1NameNode = mock(JsonNode.class);
        JsonNode ingredient2NameNode = mock(JsonNode.class);
        JsonNode ingredient3NameNode = mock(JsonNode.class);
        when(ingredient1.path("name")).thenReturn(ingredient1NameNode);
        when(ingredient2.path("name")).thenReturn(ingredient2NameNode);
        when(ingredient3.path("name")).thenReturn(ingredient3NameNode);
        when(ingredient1NameNode.asText()).thenReturn("tomato");
        when(ingredient2NameNode.asText()).thenReturn("onion");
        when(ingredient3NameNode.asText()).thenReturn("garlic");

        // Step 2: Call the method under test
        RecipeDTO result = recipeService.constructRecipeSearchByIngredient(mockJsonNode);

        // Step 3: Verify results
        assertNotNull(result);
        assertEquals(1, result.getRecipeApiId());
        assertEquals("Tomato Soup", result.getName());
        assertEquals("http://example.com/image.jpg", result.getImage());
        assertEquals(3, result.getUsedIngredients().size());
        assertTrue(result.getUsedIngredients().contains("Tomato"));
        assertTrue(result.getUsedIngredients().contains("Onion"));
        assertTrue(result.getUsedIngredients().contains("Garlic"));

        // Step 4: Verify the capitalized names of ingredients
        assertEquals("Tomato", result.getUsedIngredients().get(0));
        assertEquals("Onion", result.getUsedIngredients().get(1));
        assertEquals("Garlic", result.getUsedIngredients().get(2));
    }
}


