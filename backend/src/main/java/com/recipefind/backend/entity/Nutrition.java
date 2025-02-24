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
public class Nutrition {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;

    private String amount;

    public Nutrition(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }
}
