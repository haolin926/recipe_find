package com.recipefind.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.PredictResult;
import com.recipefind.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @GetMapping("/name")
    public ResponseEntity<List<RecipeDTO>> getRecipeByName(@RequestParam("queryName") String queryName) {

        try {
            List<RecipeDTO> recipes = recipeService.findRecipesByName(queryName);
            return ResponseEntity.ok(recipes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value="/image", consumes="multipart/form-data")
    public ResponseEntity<PredictResult> getRecipeByImage(@RequestParam("image") MultipartFile image) {
        try {
            PredictResult prediction = recipeService.imagePrediction(image);

            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/id")
    public ResponseEntity<RecipeDTO> getRecipeById(@RequestParam("queryId") Integer id) {

        try {
            RecipeDTO recipes = recipeService.findRecipeInSpoonacularByApiId(id);

            if (!(recipes == null)) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRecipe(@RequestBody RecipeDTO recipeDTO, @RequestParam("userId") Integer userId) {
        try {
            Integer result = recipeService.saveFavouriteRecipe(recipeDTO, userId);
            if (result != null) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (DataIntegrityViolationException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Recipe already saved by user");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/searchByIngredients")
    public ResponseEntity<?> searchRecipeByIngredients(@RequestParam("ingredients") List<String> ingredients) {
        try {
            List<RecipeDTO> recipes = recipeService.findRecipesByIngredients(ingredients);
            if (recipes != null) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
