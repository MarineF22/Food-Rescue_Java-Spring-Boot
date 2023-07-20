package com.fournierMarine.cgi.stage.FoodRescueApp.models;

import javax.persistence.*;
@Entity
@Table(name = "Items")
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ingredient;
    private String apiIngredient;

    public Items() {
    }

    public Items(String ingredient, String apiIngredient) {
        this.ingredient = ingredient;
        this.apiIngredient = apiIngredient;
    }

    public Long getId() {
        return id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getApiIngredient() {
        return apiIngredient;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void setApiIngredient(String apiIngredient) {
        this.apiIngredient = apiIngredient;
    }

    public String toString() {
        return "Item (" + getId() + ", " + getIngredient() + ", " + getApiIngredient() + ")";
    }

}