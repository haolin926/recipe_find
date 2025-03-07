package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "saved_recipe", schema = "recipe_db", catalog = "recipe_application")
public class SavedRecipeEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id", nullable = false)
    private Recipe recipe;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_recipe_id")
    private int savedRecipeId;
}
