package com.recipefind.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.entity.Prediction;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.PredictResult;
import com.recipefind.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/name")
    public ResponseEntity<List<RecipeDTO>> getRecipeByName(@RequestParam("queryName") String queryName) {

        try {
            List<RecipeDTO> recipes = recipeService.findRecipesByName(queryName);

            if (!(recipes.isEmpty())) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value="/image", consumes="multipart/form-data")
    public ResponseEntity<PredictResult> getRecipeByImage(@RequestParam("image") MultipartFile image) {
        try {
            PredictResult prediction = recipeService.imagePrediction(image);

            if (prediction != null) {
                return ResponseEntity.ok(prediction);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/id")
    public ResponseEntity<RecipeDTO> getRecipeById(@RequestParam("queryId") Integer id) {

        try {
            System.out.println("Query ID: " + id.toString());
            RecipeDTO recipes = recipeService.findRecipeInSpoonacularByApiId(id);

            if (!(recipes == null)) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
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
