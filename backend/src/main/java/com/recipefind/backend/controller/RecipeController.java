package com.recipefind.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipefind.backend.entity.Recipe;
import com.recipefind.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @GetMapping("/name")
    public Recipe getRecipeByName(@RequestParam("queryName") String queryName) throws JsonProcessingException {
        System.out.println("Query Name: " + queryName);
        return recipeService.constructRecipe(queryName);
    }

    @PostMapping(value="/image", consumes="multipart/form-data")
    public Recipe getRecipeByImage(@RequestParam("image") MultipartFile image) throws JsonProcessingException {
        System.out.println("File name" + image.getOriginalFilename());
        String predictedName = recipeService.predictName(image);
        System.out.println("Predicted Name: " + predictedName);
        return recipeService.constructRecipe(predictedName);
    }

}
