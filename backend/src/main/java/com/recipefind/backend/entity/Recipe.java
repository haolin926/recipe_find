package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recipes")
@Data
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", nullable = false)
    private int recipeId;

    @Column(name = "recipe_api_id", nullable = false)
    private Integer recipeApiId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "recipe_name")
    private String name;

    @Column(name = "recipe_description")
    private String description;

    @Column(name = "instruction")
    private String instruction;
}
