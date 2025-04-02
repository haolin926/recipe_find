package com.recipefind.backend.entity;

import com.recipefind.backend.utils.FractionConverter;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "recipes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "recipe_api_id", nullable = false)
    private Integer recipeApiId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "recipe_name")
    private String name;

    @Column(name = "recipe_description")
    private String description;

    @Column(name = "instruction", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> instruction;

    @Column(name = "dairy_free")
    private boolean dairyFree;

    @Column(name = "gluten_free")
    private boolean glutenFree;

    @Column(name = "vegetarian")
    private boolean vegetarian;

    @Column(name = "cook_time")
    private Integer cookTime;

    @Column(name = "rate")
    private Float rate;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredientsEntity> recipeIngredientsEntities = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeNutritionEntity> recipeNutritionEntities = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlanRecipeEntity> mealPlanRecipeEntities = new ArrayList<>();

    public RecipeDTO convertToRecipeDTO() {
        RecipeDTO recipeDTO = new RecipeDTO();
        FractionConverter fractionConverter = new FractionConverter();
        // Set basic fields
        recipeDTO.setId(this.getRecipeId().intValue());
        recipeDTO.setRecipeApiId(this.getRecipeApiId());
        recipeDTO.setName(this.getName());
        recipeDTO.setImage(this.getImageUrl());
        recipeDTO.setInstructions(this.getInstruction());
        recipeDTO.setDairyFree(this.dairyFree);
        recipeDTO.setGlutenFree(this.glutenFree);
        recipeDTO.setVegetarian(this.vegetarian);
        recipeDTO.setCookTime(this.cookTime);
        recipeDTO.setDescription(this.getDescription());
        recipeDTO.setRate(this.rate);

        // Map RecipeIngredientsEntity to IngredientDTO
        List<IngredientDTO> ingredientDTOS = this.getRecipeIngredientsEntities().stream()
                .map(recipeIngredientsEntity -> {
                    IngredientDTO ingredientDTO = new IngredientDTO();
                    ingredientDTO.setId(recipeIngredientsEntity.getIngredientsEntity().getIngredientId().intValue());
                    ingredientDTO.setName(recipeIngredientsEntity.getIngredientsEntity().getIngredientName());
                    ingredientDTO.setAmount(fractionConverter.decimalToFraction(recipeIngredientsEntity.getIngredientAmount().doubleValue()));
                    ingredientDTO.setUnit(recipeIngredientsEntity.getIngredientUnit());
                    return ingredientDTO;
                })
                .collect(Collectors.toList());
        recipeDTO.setIngredientDTOS(ingredientDTOS);

        // Map RecipeNutritionEntity to NutritionDTO
        List<NutritionDTO> nutritionDTOS = this.getRecipeNutritionEntities().stream()
                .map(recipeNutritionEntity -> {
                    NutritionDTO nutritionDTO = new NutritionDTO();
                    nutritionDTO.setId(recipeNutritionEntity.getNutritionEntity().getNutritionId());
                    nutritionDTO.setName(recipeNutritionEntity.getNutritionEntity().getNutritionName());
                    nutritionDTO.setAmount(recipeNutritionEntity.getAmount().toString());
                    nutritionDTO.setUnit(recipeNutritionEntity.getUnit());

                    return nutritionDTO;
                })
                .collect(Collectors.toList());
        recipeDTO.setNutritionDTOS(nutritionDTOS);

        return recipeDTO;
    }
}
