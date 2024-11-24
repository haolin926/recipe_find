package com.recipefind.backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recipe {
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("instructions")
    private List<String> instructions = new ArrayList<>();

    @JsonProperty("ingredients")
    private List<Map<String, String>> ingredients = new ArrayList<>();

    @JsonProperty("nutrition")
    private List<Map<String, String>> nutrition = new ArrayList<>();

    public void addInstruction(String instruction) {
        instructions.add(instruction);
    }
    public void addIngredient(String ingredient, String amount_unit) {
        Map<String, String> ingredientMap = new HashMap<>();
        ingredientMap.put("ingredient", ingredient);
        ingredientMap.put("amount_unit", amount_unit);
        ingredients.add(ingredientMap);
    }

    public void addNutrition(String nutrient, String amount_unit) {
        Map<String, String> nutritionMap = new HashMap<>();
        nutritionMap.put("nutrient", nutrient);
        nutritionMap.put("amount_unit", amount_unit);
        nutrition.add(nutritionMap);
    }

}
