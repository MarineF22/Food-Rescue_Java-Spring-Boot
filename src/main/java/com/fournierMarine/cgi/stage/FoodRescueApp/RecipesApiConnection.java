package com.fournierMarine.cgi.stage.FoodRescueApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipesApiConnection {

    /**
     * Gets a list of recipes based on the ingredient list provided.
     *
     * @param extractedApiIngredientsList List of ingredients extracted from the PDF analysis.
     * @return List of recipe maps containing the recipe information.
     */
    public static ArrayList<Map<String, String>> getRecipesList(ArrayList<String> extractedApiIngredientsList) {
        ArrayList<Map<String, String>> recipesList = new ArrayList<>();
        Set<String> addedRecipes = new HashSet<>(); // Set to store added recipes

        for (String ingredient : extractedApiIngredientsList) {
            String apiUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?i=" + ingredient;
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Processes the response to get the list of prescription maps
                    ArrayList<Map<String, String>> recipesMapList = getRecipesMapList(response.toString(), ingredient);

                    // Add only unique recipes to the main list
                    for (Map<String, String> recipe : recipesMapList) {
                        String recipeId = recipe.get("idMeal");
                        if (!addedRecipes.contains(recipeId)) {
                            recipesList.add(recipe);
                            addedRecipes.add(recipeId);
                        }
                    }
                } else {
                    System.out.println("Failed to fetch data for ingredient " + ingredient + ". Response code: " + responseCode);
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return recipesList;
    }

    /**
     * Processes the API response and creates the list of recipe maps.
     *
     * @param response   API response.
     * @param ingredient Ingredient associated with recipes.
     * @return List of recipe maps containing the recipe information.
     */
    private static ArrayList<Map<String, String>> getRecipesMapList(String response, String ingredient) {
        ArrayList<Map<String, String>> recipesMapList = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray mealsArray = jsonResponse.getJSONArray("meals");

            for (int i = 0; i < mealsArray.length(); i++) {
                JSONObject meal = mealsArray.getJSONObject(i);
                Map<String, String> recipe = new HashMap<>();
                recipe.put("Ingredient", ingredient);
                recipe.put("idMeal", meal.getString("idMeal"));
                recipe.put("strMealThumb", meal.getString("strMealThumb"));
                recipe.put("strMeal", meal.getString("strMeal"));
                recipesMapList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipesMapList;
    }


    public static Map<String, String> getRecipeMap(String idMeal) {
        Map<String, String> recipeMap = new HashMap<>();

        String apiUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + idMeal;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println(response.toString());

                // Process the recipe JSON response and populate the recipe map
                recipeMap = getRecipeMapFromJSON(response.toString());

            } else {
                System.out.println("Failed to fetch data for idMeal " + idMeal + ". Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipeMap;
    }

    public static Map<String, String> getRecipeMapFromJSON(String input) {
        Map<String, String> recipeMap = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(input);
            JSONArray mealsArray = jsonObject.getJSONArray("meals");
            System.out.println("mealsArray.length() : "+mealsArray.length());
            if (mealsArray.length() > 0) {
                JSONObject meal = mealsArray.getJSONObject(0);

                recipeMap.put("strMeal", meal.optString("strMeal", ""));
                recipeMap.put("strCategory", meal.optString("strCategory", ""));
                recipeMap.put("strArea", meal.optString("strArea", ""));
                recipeMap.put("strInstructions", meal.optString("strInstructions", ""));
                recipeMap.put("strMealThumb", meal.optString("strMealThumb", ""));
                recipeMap.put("strYoutube", meal.optString("strYoutube", ""));

                // Process strIngredient and strMeasure dynamically
                // Process strIngredient dynamically
                int i = 1;
                while (meal.has("strIngredient" + i)) {
                    String ingredientKey = "strIngredient" + i;
                    String ingredient = meal.optString(ingredientKey, "");

                    if (!ingredient.isEmpty()) {
                        recipeMap.put(ingredientKey, ingredient);
                    } else {
                        break; // Exit loop if a key is missing or empty
                    }

                    i++;
                }

                // Process strMeasure dynamically
                i = 1;
                while (meal.has("strMeasure" + i)) {
                    String measureKey = "strMeasure" + i;
                    String measure = meal.optString(measureKey, "");

                    if (!measure.isEmpty()) {
                        recipeMap.put(measureKey, measure);
                    } else {
                        break; // Exit loop if a key is missing or empty
                    }

                    i++;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipeMap;
    }
}