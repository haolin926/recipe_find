package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "recipe_nutrition", schema = "recipe_db", catalog = "recipe_application")
@Data
public class RecipeNutritionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "recipe_nutrition_id")
    private int recipeNutritionId;
    @Basic
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic
    @Column(name = "unit")
    private String unit;

    @ManyToOne
    @JoinColumn(name = "nutrition_id", referencedColumnName = "nutrition_id", nullable = false)
    private NutritionEntity nutritionEntity;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id", nullable = false)
    private Recipe recipe;
}
