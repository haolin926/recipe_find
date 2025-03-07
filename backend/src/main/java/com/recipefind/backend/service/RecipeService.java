package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.PredictResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    RecipeDTO constructRecipeFromComplexSearch(JsonNode recipe) throws JsonProcessingException;

    List<RecipeDTO> findRecipesByName(String name) throws JsonProcessingException;

    RecipeDTO findRecipeById(Integer recipeId) throws JsonProcessingException;

    PredictResult imagePrediction (MultipartFile image) throws Exception;

    Integer saveFavouriteRecipe(RecipeDTO recipeDTO, Integer userId);

    //TODO: move this method to SavedRecipeService
    List<RecipeDTO> getSavedRecipes(Integer userId) throws Exception;

    List<RecipeDTO>findRecipesByIngredients(List<String> ingredients) throws JsonProcessingException;

}
