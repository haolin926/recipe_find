package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
@Entity
@Table(name = "recipe_ingredients", schema = "recipe_db", catalog = "recipe_application")
@Data
public class RecipeIngredientsEntity {
    @Basic
    @Column(name = "ingredient_amount")
    private BigDecimal ingredientAmount;
    @Basic
    @Column(name = "ingredient_unit")
    private String ingredientUnit;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "recipe_ingredient_id")
    private int recipeIngredientId;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredient_id", nullable = false)
    private IngredientsEntity ingredientsEntity;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id", nullable = false)
    private Recipe recipe;
}
