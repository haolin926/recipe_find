package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@jakarta.persistence.Table(name = "ingredients", schema = "recipe_db", catalog = "recipe_application")
@Data
public class IngredientsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "ingredient_id")
    private Long ingredientId;
    @Basic
    @Column(name = "ingredient_name")
    private String ingredientName;

}
