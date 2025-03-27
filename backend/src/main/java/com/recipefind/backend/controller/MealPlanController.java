package com.recipefind.backend.controller;

import com.recipefind.backend.entity.MealPlanDTO;
import com.recipefind.backend.entity.MealPlanEntity;
import com.recipefind.backend.entity.MealPlanWeeklySummaryDTO;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/mealplan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final Logger logger = LoggerFactory.getLogger(MealPlanController.class);

    @GetMapping("/ondate")
    public ResponseEntity<?> getMealPlanForUserOnDate (@RequestParam("userId") Integer userId, @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("date") Date date) {
        try {
            MealPlanDTO mealPlanDTO = mealPlanService.getMealPlanForUserOnDate(userId, date);

            if (mealPlanDTO != null) {
                return ResponseEntity.ok(mealPlanDTO);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get meal plans for user");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get meal plans for user");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMealPlanForUserOnDate (@RequestParam("userId") Integer userId, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @RequestBody RecipeDTO recipeDTO) {
        try {
            MealPlanEntity mealPlanEntity = mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);

            if (mealPlanEntity.getMealPlanId() != null) {
                return ResponseEntity.ok("Recipe successfully added into meal plan");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add recipe into meal plan");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add recipe into meal plan");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> deleteRecipeOnMealPlan(@RequestParam("mealPlanId") Integer mealPlanId, @RequestParam("recipeId") Integer recipeId ) {
        try {
            MealPlanDTO mealPlanDTO = mealPlanService.DeleteRecipeForMealPlan(mealPlanId, recipeId);

            if (mealPlanDTO != null) {
                return ResponseEntity.ok("Recipe successfully deleted from meal plan");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete recipe from meal plan");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete recipe from meal plan");
        }
    }

    @GetMapping("/getWeekSummary")
    public ResponseEntity<?> getWeeklySummary(@RequestParam("userId") Integer userId, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        try {
            MealPlanWeeklySummaryDTO mealPlanWeeklySummaryDTO = mealPlanService.getMealPlanForUserOnCurrentWeek(userId, date);

            if (mealPlanWeeklySummaryDTO != null) {
                return ResponseEntity.ok(mealPlanWeeklySummaryDTO);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete meal plan week summary");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
