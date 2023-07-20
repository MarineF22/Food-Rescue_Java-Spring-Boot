package com.fournierMarine.cgi.stage.FoodRescueApp.component;

import com.fournierMarine.cgi.stage.FoodRescueApp.DataBaseQueries;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.ExpirationDateItem;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.UsersRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;



@Component
public class UserIngredients {
    private static UsersRepository usersRepository;

    public UserIngredients(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }
    public static void addToListUsers(List<String> extractedApiIngredientsList, String email, DataBaseQueries query) {

        List<Items> listToAddToUsers = new ArrayList<>(); // Temporary list to store items from the analyzed invoice.

        for (String ingredientName : extractedApiIngredientsList) { // This loop reads each of the items collected from the invoice.
            Items item = query.getFindByApiIngredient(ingredientName); // Retrieve the corresponding ingredient from the itemsRepository based on the collected apiIngredient from the invoice.
            if (item != null) { // If the ingredient exists in the database
                listToAddToUsers.add(item); // Add it to the temporary list
            } else {
                System.out.println("No items found for ingredient: " + ingredientName);
            }
            System.out.println("listToAddToUsers:   " + listToAddToUsers);
        }

        Users user = query.getUser(email); // Retrieve the currently logged-in user

        for (Items item : listToAddToUsers) {
            ExpirationDateItem dateItem = new ExpirationDateItem();
            dateItem.setItems(item);
            dateItem.setUsers(user);
            user.getListDateItems().add(dateItem);

        }

        usersRepository.save(user); // Save the changes to the database

        listToAddToUsers.clear(); // Clear the items from the temporary list
    }
}
