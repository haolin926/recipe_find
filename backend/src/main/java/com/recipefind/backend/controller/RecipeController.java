package com.recipefind.backend.controller;


import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.entity.PredictResult;
import com.recipefind.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @GetMapping("/name")
    public ResponseEntity<List<RecipeDTO>> getRecipeByName(@RequestParam("queryName") String queryName) {

        try {
            System.out.println("Query Name: " + queryName);
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
    public PredictResult getRecipeByImage(@RequestParam("image") MultipartFile image) throws Exception {
        System.out.println("File name" + image.getOriginalFilename());
        return recipeService.imagePrediction(image);
    }


    @GetMapping("/id")
    public ResponseEntity<RecipeDTO> getRecipeById(@RequestParam("queryId") Integer id) {

        try {
            System.out.println("Query ID: " + id.toString());
            RecipeDTO recipes = recipeService.findRecipeById(id);

            if (!(recipes == null)) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
