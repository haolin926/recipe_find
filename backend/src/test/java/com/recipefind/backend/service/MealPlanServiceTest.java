package com.recipefind.backend.service;

import com.recipefind.backend.dao.MealPlanRecipeRepository;
import com.recipefind.backend.dao.MealPlanRepository;
import com.recipefind.backend.entity.*;
import com.recipefind.backend.service.Impl.MealPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MealPlanServiceTest {

    @InjectMocks
    private MealPlanServiceImpl mealPlanService;

    @Mock
    private UserService userService;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private RecipeService recipeService;

    @Mock
    private MealPlanRecipeRepository mealPlanRecipeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMealPlanForUserOnDate_Success() throws Exception {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        User user = new User();
        user.setId(userId.longValue());
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanRecipes(new ArrayList<>());
        when(userService.getUserById(userId)).thenReturn(user);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(mealPlanEntity);

        // Act
        MealPlanDTO result = mealPlanService.getMealPlanForUserOnDate(userId, date);

        // Assert
        assertNotNull(result);
        verify(userService, times(1)).getUserById(userId);
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
    }

    @Test
    void testGetMealPlanForUserOnDate_UserNotFound() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        when(userService.getUserById(userId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mealPlanService.getMealPlanForUserOnDate(userId, date);
        });
        assertEquals("Failed in Getting meal plan due to user is not valid", exception.getMessage());
        verify(userService, times(1)).getUserById(userId);
        verify(mealPlanRepository, never()).getMealPlanEntityByUserAndPlannedDate(any(User.class), any(Date.class));
    }

    @Test
    void testGetMealPlanForUserOnDate_MealPlanNotFound() throws Exception {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        User user = new User();
        user.setId(userId.longValue());
        when(userService.getUserById(userId)).thenReturn(user);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(null);

        // Act
        MealPlanDTO result = mealPlanService.getMealPlanForUserOnDate(userId, date);

        // Assert
        assertNotNull(result);
        assertNull(result.getRecipeDTOList());
        verify(userService, times(1)).getUserById(userId);
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
    }

    @Test
    void testAddRecipeIntoMealPlan_Success() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        User user = new User();
        user.setId(userId.longValue());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        mealPlanEntity.setUser(user);
        mealPlanEntity.setPlannedDate(date);

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlanRecipeId(1);
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(recipe);

        when(userService.getUserById(userId)).thenReturn(user);
        when(recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId())).thenReturn(recipe);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(mealPlanEntity);
        when(mealPlanRecipeRepository.save(any(MealPlanRecipeEntity.class))).thenReturn(mealPlanRecipeEntity);

        // Act
        MealPlanEntity result = mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);

        // Assert
        assertNotNull(result);
        assertEquals(mealPlanEntity.getMealPlanId(), result.getMealPlanId());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, times(1)).findRecipeByApiId(recipeDTO.getRecipeApiId());
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
        verify(mealPlanRecipeRepository, times(1)).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testAddRecipeIntoMealPlan_UserNotFound() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        when(userService.getUserById(userId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, never()).findRecipeByApiId(anyInt());
        verify(mealPlanRepository, never()).getMealPlanEntityByUserAndPlannedDate(any(User.class), any(Date.class));
        verify(mealPlanRecipeRepository, never()).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testAddRecipeIntoMealPlan_RecipeNotFound() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        User user = new User();
        user.setId(userId.longValue());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        mealPlanEntity.setUser(user);
        mealPlanEntity.setPlannedDate(date);

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlanRecipeId(1);
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(recipe);

        when(userService.getUserById(userId)).thenReturn(user);
        when(recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId())).thenReturn(null);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(mealPlanEntity);
        when(mealPlanRecipeRepository.save(any(MealPlanRecipeEntity.class))).thenReturn(mealPlanRecipeEntity);
        // Act
        MealPlanEntity result = mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);

        assertEquals(result.getMealPlanId(), mealPlanEntity.getMealPlanId());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, times(1)).findRecipeByApiId(recipeDTO.getRecipeApiId());
        verify(recipeService, times(1)).saveRecipe(recipeDTO);
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
        verify(mealPlanRecipeRepository, times(1)).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testAddRecipeIntoMealPlan_MealPlanNotExist() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        User user = new User();
        user.setId(userId.longValue());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        mealPlanEntity.setUser(user);
        mealPlanEntity.setPlannedDate(date);

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlanRecipeId(1);
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(recipe);

        when(userService.getUserById(userId)).thenReturn(user);
        when(recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId())).thenReturn(recipe);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(null);
        when(mealPlanRepository.save(any(MealPlanEntity.class))).thenReturn(mealPlanEntity);
        when(mealPlanRecipeRepository.save(any(MealPlanRecipeEntity.class))).thenReturn(mealPlanRecipeEntity);

        // Act
        MealPlanEntity result = mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);

        assertEquals(result.getMealPlanId(), mealPlanEntity.getMealPlanId());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, times(1)).findRecipeByApiId(recipeDTO.getRecipeApiId());
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
        verify(mealPlanRepository, times(1)).save(any(MealPlanEntity.class));
        verify(mealPlanRecipeRepository, times(1)).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testAddRecipeIntoMealPlan_MealPlanSaveFails() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        User user = new User();
        user.setId(userId.longValue());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        when(userService.getUserById(userId)).thenReturn(user);
        when(recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId())).thenReturn(recipe);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(null);
        when(mealPlanRepository.save(any(MealPlanEntity.class))).thenThrow(new RuntimeException("Failed to save MealPlanEntity"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);
        });
        assertEquals("Failed to save MealPlanEntity", exception.getMessage());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, times(1)).findRecipeByApiId(recipeDTO.getRecipeApiId());
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
        verify(mealPlanRepository, times(1)).save(any(MealPlanEntity.class));
        verify(mealPlanRecipeRepository, never()).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testAddRecipeIntoMealPlan_MealPlanRecipeSaveFails() {
        // Arrange
        Integer userId = 1;
        Date date = new Date();
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeApiId(1);

        User user = new User();
        user.setId(userId.longValue());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        mealPlanEntity.setUser(user);
        mealPlanEntity.setPlannedDate(date);

        when(userService.getUserById(userId)).thenReturn(user);
        when(recipeService.findRecipeByApiId(recipeDTO.getRecipeApiId())).thenReturn(recipe);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDate(user, date)).thenReturn(mealPlanEntity);
        when(mealPlanRecipeRepository.save(any(MealPlanRecipeEntity.class))).thenThrow(new RuntimeException("Failed to save MealPlanRecipeEntity"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mealPlanService.AddRecipeIntoMealPlan(userId, date, recipeDTO);
        });
        assertEquals("Failed to save MealPlanRecipeEntity", exception.getMessage());
        verify(userService, times(1)).getUserById(userId);
        verify(recipeService, times(1)).findRecipeByApiId(recipeDTO.getRecipeApiId());
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDate(user, date);
        verify(mealPlanRecipeRepository, times(1)).save(any(MealPlanRecipeEntity.class));
    }

    @Test
    void testDeleteRecipeForMealPlan_Success() {
        // Given: Meal plan and recipe exist
        Integer mealPlanId = 1;
        Integer recipeId = 10;

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(mealPlanId);
        mealPlanEntity.setMealPlanRecipes(new ArrayList<>());

        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeId.longValue());

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(recipe);

        when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlanEntity));
        when(mealPlanRecipeRepository.findByMealPlan_MealPlanIdAndRecipe_RecipeId(mealPlanId, recipeId.longValue()))
                .thenReturn(mealPlanRecipeEntity);
        when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlanEntity));

        // When: Deleting the recipe from the meal plan
        MealPlanDTO result = mealPlanService.DeleteRecipeForMealPlan(mealPlanId, recipeId);

        // Then: Verify interactions and assertions
        verify(mealPlanRecipeRepository, times(1)).delete(mealPlanRecipeEntity);
        assertNotNull(result);
        assertEquals(mealPlanId, result.getId());
    }

    @Test
    void testDeleteRecipeForMealPlan_MealPlanNotFound() {
        // Given: Meal plan ID that doesn't exist
        Integer mealPlanId = 2;
        Integer recipeId = 10;

        when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.empty());

        // When & Then: Expect exception when meal plan is not found
        Exception exception = assertThrows(RuntimeException.class, () ->
                mealPlanService.DeleteRecipeForMealPlan(mealPlanId, recipeId)
        );

        assertEquals("Meal Plan not found", exception.getMessage());
        verify(mealPlanRecipeRepository, never()).delete(any());
    }

    @Test
    void testDeleteRecipeForMealPlan_RecipeNotFound() {
        // Given: Meal plan exists but recipe is not found
        Integer mealPlanId = 1;
        Integer recipeId = 10;

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(mealPlanId);

        when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlanEntity));
        when(mealPlanRecipeRepository.findByMealPlan_MealPlanIdAndRecipe_RecipeId(mealPlanId, recipeId.longValue()))
                .thenReturn(null);

        // When & Then: Expect exception when recipe is not in the meal plan
        Exception exception = assertThrows(RuntimeException.class, () ->
                mealPlanService.DeleteRecipeForMealPlan(mealPlanId, recipeId)
        );

        assertEquals("Recipe not found in the Meal Plan", exception.getMessage());
        verify(mealPlanRecipeRepository, never()).delete(any());
    }

    @Test
    void testDeleteRecipeForMealPlan_FailedToSaveMealPlanRecipeEntity() {
        // Given: Meal plan exists and recipe exists but fails to retrieve updated meal plan
        Integer mealPlanId = 1;
        Integer recipeId = 10;

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(mealPlanId);

        MealPlanRecipeEntity mealPlanRecipeEntity = new MealPlanRecipeEntity();
        mealPlanRecipeEntity.setMealPlan(mealPlanEntity);
        mealPlanRecipeEntity.setRecipe(new Recipe());

        // Mocking behavior
        when(mealPlanRepository.findById(mealPlanId)).thenReturn(Optional.of(mealPlanEntity));
        when(mealPlanRecipeRepository.findByMealPlan_MealPlanIdAndRecipe_RecipeId(mealPlanId, recipeId.longValue()))
                .thenReturn(mealPlanRecipeEntity);
        doThrow(new RuntimeException("Failed to delete Recipe from Meal Plan")).when(mealPlanRecipeRepository).delete(mealPlanRecipeEntity); // Simulate failed save

        // When & Then: Expect exception when meal plan retrieval fails after deletion
        Exception exception = assertThrows(RuntimeException.class, () ->
                mealPlanService.DeleteRecipeForMealPlan(mealPlanId, recipeId)
        );

        assertEquals("Failed to delete Recipe from Meal Plan", exception.getMessage());
    }

    @Test
    void testGetMealPlanForUserOnCurrentWeek_Success() {
        // Given: User and meal plans exist
        Integer userId = 1;
        Date date = new Date();

        User user = new User();
        user.setId(userId.longValue());

        MealPlanEntity mealPlanEntity1 = new MealPlanEntity();
        mealPlanEntity1.setMealPlanId(1);
        mealPlanEntity1.setPlannedDate(date);
        mealPlanEntity1.setUser(user);
        mealPlanEntity1.setMealPlanRecipes(new ArrayList<>());

        MealPlanEntity mealPlanEntity2 = new MealPlanEntity();
        mealPlanEntity2.setMealPlanId(2);
        mealPlanEntity2.setPlannedDate(date);
        mealPlanEntity2.setUser(user);
        mealPlanEntity2.setMealPlanRecipes(new ArrayList<>());

        List<MealPlanEntity> mealPlanEntityList = Arrays.asList(mealPlanEntity1, mealPlanEntity2);

        when(userService.getUserById(userId)).thenReturn(user);
        when(mealPlanRepository.getMealPlanEntityByUserAndPlannedDateBetween(any(User.class), any(Date.class), any(Date.class)))
                .thenReturn(mealPlanEntityList);

        // When: Retrieving the meal plan for the current week
        MealPlanWeeklySummaryDTO result = mealPlanService.getMealPlanForUserOnCurrentWeek(userId, date);

        // Then: Verify interactions and assertions
        assertNotNull(result);
        verify(userService, times(1)).getUserById(userId);
        verify(mealPlanRepository, times(1)).getMealPlanEntityByUserAndPlannedDateBetween(any(User.class), any(Date.class), any(Date.class));
    }

    @Test
    void testGetMealPlanForUserOnCurrentWeek_UserNotFound() {
        // Given: User does not exist
        Integer userId = 1;
        Date date = new Date();

        when(userService.getUserById(userId)).thenReturn(null);

        // When & Then: Expect exception when user is not found
        Exception exception = assertThrows(RuntimeException.class, () ->
                mealPlanService.getMealPlanForUserOnCurrentWeek(userId, date)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserById(userId);
        verify(mealPlanRepository, never()).getMealPlanEntityByUserAndPlannedDateBetween(any(User.class), any(Date.class), any(Date.class));
    }
}
