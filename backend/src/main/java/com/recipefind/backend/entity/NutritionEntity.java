package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nutrition", schema = "recipe_db", catalog = "recipe_application")
@Data
public class NutritionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "nutrition_id")
    private Integer nutritionId;
    @Basic
    @Column(name = "nutrition_name")
    private String nutritionName;
}
