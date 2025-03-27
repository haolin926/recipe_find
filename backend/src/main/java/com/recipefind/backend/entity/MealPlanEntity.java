package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "meal_plans", schema = "recipe_db", catalog = "recipe_application")
public class MealPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_plan_id")
    private Integer mealPlanId;

    @Column(name = "plan_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date plannedDate;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlanRecipeEntity> mealPlanRecipes;

}
