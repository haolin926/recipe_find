package com.recipefind.backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDTO {
    private Integer id;

    private Integer recipeApiId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("instructions")
    private List<String> instructions = new ArrayList<>();

    @JsonProperty("ingredients")
    private List<IngredientDTO> ingredientDTOS = new ArrayList<>();

    @JsonProperty("nutrition")
    private List<NutritionDTO> nutritionDTOS = new ArrayList<>();

    private String description;

    private boolean dairyFree;

    private boolean glutenFree;

    private boolean vegetarian;

    private Integer cookTime;

    private List<String> usedIngredients;

    private Float rate;
}
