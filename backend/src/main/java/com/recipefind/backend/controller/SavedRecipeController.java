package com.recipefind.backend.controller;

import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.SaveRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savedRecipe")
@RequiredArgsConstructor
public class SavedRecipeController {

    private final SaveRecipeService saveRecipeService;
    @GetMapping
    public ResponseEntity<?> getSavedRecipes(@RequestParam("userId")Integer userId) {
        try {
            List<RecipeDTO> recipes = saveRecipeService.findByUser(userId);

            if (recipes != null) {
                return ResponseEntity.ok(recipes);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSavedRecipe(@RequestParam("userId")Integer userId, @RequestParam("recipeId")Integer recipeId)
    {
        try {
            boolean isDeleted = saveRecipeService.deleteSavedRecipe(userId, recipeId);
            if (isDeleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
