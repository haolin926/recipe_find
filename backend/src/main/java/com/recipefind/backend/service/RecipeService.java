package com.recipefind.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.PredictResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {

    List<RecipeDTO> findRecipesByName(String name) throws JsonProcessingException;

    RecipeDTO findRecipeInSpoonacularByApiId(Integer recipeApiId) throws JsonProcessingException;

    Recipe findRecipeById (Integer recipeId);

    PredictResult imagePrediction (MultipartFile image) throws Exception;

    Integer saveFavouriteRecipe(RecipeDTO recipeDTO, Integer userId);

    List<RecipeDTO>findRecipesByIngredients(List<String> ingredients) throws JsonProcessingException;

    Recipe saveRecipe(RecipeDTO recipeDTO);

    Recipe findRecipeByApiId(Integer recipeApiId);

    Boolean updateRecipeRate(Long recipeId, Float rate);
}
