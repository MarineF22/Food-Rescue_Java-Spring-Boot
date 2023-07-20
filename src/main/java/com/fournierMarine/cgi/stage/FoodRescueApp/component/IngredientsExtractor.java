package com.fournierMarine.cgi.stage.FoodRescueApp.component;

import com.fournierMarine.cgi.stage.FoodRescueApp.DataBaseQueries;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientsExtractor {

    public static ArrayList<Items> getFilteredIngredients(ArrayList<String> itemsList, DataBaseQueries query) {
        ArrayList<Items> filteredIngredients = new ArrayList<>();

        for (String ingredient : itemsList) {
            System.out.println("Initial Ingredient: " + ingredient);

            List<Items> filteredIngredient = query.getFilteredIngredient(ingredient);

            if (filteredIngredient.isEmpty()) {
                String[] ingredientSplit = ingredient.split(" ");
                StringBuilder partialString = new StringBuilder();
                boolean found = false;

                for (int i = 0; i < ingredientSplit.length; i++) {
                    partialString.append(ingredientSplit[i]);
                    if (i != ingredientSplit.length - 1) {
                        filteredIngredient = query.getFilteredIngredient(partialString.toString());

                        if (!filteredIngredient.isEmpty()) {
                            found = true;
                            break;
                        }

                        partialString.append(" ");
                    }
                }

                if (!found) {
                    for (int i = ingredientSplit.length - 2; i >= 0; i--) {
                        partialString = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            partialString.append(ingredientSplit[j]);
                            if (j != i) {
                                partialString.append(" ");
                            }
                        }
                        filteredIngredient = query.getFilteredIngredient(partialString.toString());
                        if (!filteredIngredient.isEmpty()) {
                            found = true;
                            break;
                        }
                    }
                }
            }

            filteredIngredients.addAll(filteredIngredient);
            System.out.println(filteredIngredient);
        }

        return filteredIngredients;
    }

    public static ArrayList<String> getExtractedApiIngredientsList(ArrayList<Items> filteredIngredients) {
        ArrayList<String> extractedApiIngredientsList = new ArrayList<>();
        // Extract ingredients from the filteredIngredients list into a new list
        for (Items item : filteredIngredients) {
            extractedApiIngredientsList.add(item.getApiIngredient());
        }
        return extractedApiIngredientsList;
    }

}
