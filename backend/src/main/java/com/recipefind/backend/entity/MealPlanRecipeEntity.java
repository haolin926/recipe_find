package com.recipefind.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="meal_plan_recipes", schema = "recipe_db", catalog = "recipe_application")
public class MealPlanRecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_plan_recipe_id")
    private Integer mealPlanRecipeId;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id", referencedColumnName = "meal_plan_id", nullable = false)
    private MealPlanEntity mealPlan;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id", nullable = false)
    private Recipe recipe;
}
