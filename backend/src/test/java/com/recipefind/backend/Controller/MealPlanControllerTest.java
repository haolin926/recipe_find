package com.recipefind.backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.MealPlanController;
import com.recipefind.backend.entity.MealPlanDTO;
import com.recipefind.backend.entity.MealPlanEntity;
import com.recipefind.backend.entity.MealPlanWeeklySummaryDTO;
import com.recipefind.backend.entity.RecipeDTO;
import com.recipefind.backend.service.MealPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealPlanController.class)
public class MealPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealPlanService mealPlanService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMealPlanForUserOnDate_ShouldReturnMealPlan() throws Exception {
        // Arrange
        MealPlanDTO mealPlanDTO = new MealPlanDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2025-02-10");

        when(mealPlanService.getMealPlanForUserOnDate(1, date)).thenReturn(mealPlanDTO);

        // Act
        mockMvc.perform(get("/api/mealplan/ondate")
                        .param("userId", "1")
                        .param("date", "2025-02-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mealPlanDTO)));
    }

    @Test
    public void getMealPlanForUserOnDate_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(mealPlanService.getMealPlanForUserOnDate(1, new Date())).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(get("/api/mealplan/ondate")
                        .param("userId", "1")
                        .param("date", "2023-10-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateMealPlanForUserOnDate_ShouldReturnSuccess() throws Exception {
        // Arrange
        RecipeDTO recipeDTO = new RecipeDTO();
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanId(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2025-02-10");
        when(mealPlanService.AddRecipeIntoMealPlan(1, date, recipeDTO)).thenReturn(mealPlanEntity);

        // Act
        mockMvc.perform(put("/api/mealplan/update")
                        .param("userId", "1")
                        .param("date", "2025-02-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe successfully added into meal plan"));
    }

    @Test
    public void updateMealPlanForUserOnDate_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        RecipeDTO recipeDTO = new RecipeDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2025-02-10");

        when(mealPlanService.AddRecipeIntoMealPlan(1, date, recipeDTO)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(put("/api/mealplan/update")
                        .param("userId", "1")
                        .param("date", "2025-02-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteRecipeOnMealPlan_ShouldReturnSuccess() throws Exception {
        // Arrange
        MealPlanDTO mealPlanDTO = new MealPlanDTO();
        when(mealPlanService.DeleteRecipeForMealPlan(1, 1)).thenReturn(mealPlanDTO);

        // Act
        mockMvc.perform(delete("/api/mealplan/remove")
                        .param("mealPlanId", "1")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe successfully deleted from meal plan"));
    }

    @Test
    public void deleteRecipeOnMealPlan_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(mealPlanService.DeleteRecipeForMealPlan(1, 1)).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(delete("/api/mealplan/remove")
                        .param("mealPlanId", "1")
                        .param("recipeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getWeeklySummary_ShouldReturnSummary() throws Exception {
        // Arrange
        MealPlanWeeklySummaryDTO summaryDTO = new MealPlanWeeklySummaryDTO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2025-02-10");

        when(mealPlanService.getMealPlanForUserOnCurrentWeek(1, date)).thenReturn(summaryDTO);

        // Act
        mockMvc.perform(get("/api/mealplan/getWeekSummary")
                        .param("userId", "1")
                        .param("date", "2025-02-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(summaryDTO)));
    }

    @Test
    public void getWeeklySummary_InternalServerError_ShouldReturn500() throws Exception {
        // Arrange
        when(mealPlanService.getMealPlanForUserOnCurrentWeek(1, new Date())).thenThrow(new RuntimeException("Simulated server error"));

        // Act
        mockMvc.perform(get("/api/mealplan/getWeekSummary")
                        .param("userId", "1")
                        .param("date", "2023-10-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}