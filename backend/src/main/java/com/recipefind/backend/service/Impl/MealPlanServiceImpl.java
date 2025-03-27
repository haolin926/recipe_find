package com.recipefind.backend.service.Impl;

import com.recipefind.backend.converter.MealPlanConverter;
import com.recipefind.backend.dao.MealPlanRecipeRepository;
import com.recipefind.backend.dao.MealPlanRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.MealPlanService;
import com.recipefind.backend.service.RecipeService;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.recipefind.backend.converter.MealPlanConverter.convertDTOListToWeeklySummary;
import static com.recipefind.backend.converter.MealPlanConverter.convertToDTO;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

    private final UserService userService;
    private final MealPlanRepository mealPlanRepository;
    private final RecipeService recipeService;
    private final MealPlanRecipeRepository mealPlanRecipeRepository;


    @Override
    public MealPlanDTO getMealPlanForUserOnDate (Integer userId, Date date) throws Exception {
        User user = userService.getUserById(userId);

        if (user != null) {
            try {
               MealPlanEntity mealPlanEntity = mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date);

               if (mealPlanEntity != null) {
                   return convertToDTO(mealPlanEntity);
               } else {
                   return new MealPlanDTO();
               }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            throw new Exception("Failed in Getting meal plan due to user is not valid");
        }
    }

    @Override
    @Transactional
    public MealPlanEntity AddRecipeIntoMealPlan(Integer userId, Date date, RecipeDTO recipeDTO) {
        User user = userService.getUserById(userId);

        // check if user exist
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Recipe savedRecipe = recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId());

        // check if recipe already exist in db, if not then save it
        if (savedRecipe == null) {
            savedRecipe = recipeService.saveRecipe(recipeDTO);
        }

        //check if mealplan exist
        MealPlanEntity mealPlanEntity = mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date);

        if (mealPlanEntity == null) {

            mealPlanEntity = new MealPlanEntity();
            mealPlanEntity.setPlannedDate(date);
            mealPlanEntity.setUser(user);

            mealPlanEntity = mealPlanRepository.save(mealPlanEntity);
        }

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(savedRecipe);

        mealPlanRecipeRepository.save(mealPlanRecipeEntity);

        return mealPlanEntity;
    }

    @Override
    @Transactional
    public MealPlanDTO DeleteRecipeForMealPlan(Integer mealPlanId, Integer recipeId) {
        MealPlanEntity mealPlanEntity = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new RuntimeException("Meal Plan not found"));

        if (mealPlanEntity == null) {
            throw new RuntimeException("Meal Plan nt found");
        }

        MealPlanRecipeEntity mealPlanRecipeEntity = mealPlanRecipeRepository.findByMealPlan_MealPlanIdAndRecipe_RecipeId(mealPlanId, recipeId.longValue());

        if (mealPlanRecipeEntity == null) {
            throw new RuntimeException("Recipe not found in the Meal Plan");
        }

        try {
            mealPlanRecipeRepository.delete(mealPlanRecipeEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Recipe from Meal Plan");
        }

        // Step 4: Return updated MealPlanEntity
        MealPlanEntity updatedMealPlan = mealPlanRepository.findById(mealPlanId).orElse(null);
        if (updatedMealPlan != null) {
            return convertToDTO(updatedMealPlan);
        } else {
            throw new RuntimeException("Failed to save MealPlanRecipeEntity");
        }
    }

    @Override
    public MealPlanWeeklySummaryDTO getMealPlanForUserOnCurrentWeek(Integer userId, Date date) {
        User user = userService.getUserById(userId);

        // check if user exist
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Get the Monday and Sunday of the current week
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Date mondayDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date sundayDate = Date.from(sunday.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<MealPlanEntity> mealPlanEntityList = mealPlanRepository.getMealPlanEntityByUserAndPlannedDateBetween(user, mondayDate, sundayDate);

        List<MealPlanDTO> mealPlanDTOList = mealPlanEntityList.stream().map(MealPlanConverter::convertToDTO).toList();

        return convertDTOListToWeeklySummary(mealPlanDTOList);
    }
}
