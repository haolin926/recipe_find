package com.recipefind.backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("unit")
    private String unit;

    public IngredientDTO(String name, String amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }
}
