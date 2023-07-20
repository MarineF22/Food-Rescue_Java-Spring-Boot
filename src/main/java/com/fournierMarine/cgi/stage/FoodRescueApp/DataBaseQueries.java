package com.fournierMarine.cgi.stage.FoodRescueApp;

import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.ItemsRepository;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.UsersRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DataBaseQueries {
    private ItemsRepository itemsRepository;
    private UsersRepository usersRepository;

    public DataBaseQueries(ItemsRepository itemsRepository, UsersRepository usersRepository) {
        this.itemsRepository = itemsRepository;
        this.usersRepository = usersRepository;
    }

    public Users getUser(String email) {
        Users user = usersRepository.findByEmail(email);
        return user;
    }

    public boolean checkUser(String email) {
        return usersRepository.existsByEmail(email);
    }

    public ArrayList<Items> getFilteredIngredient(String ingredient) {
        return itemsRepository.findByIngredientContainingIgnoreCase(ingredient);
    }

    public Items getFindByApiIngredient(String apiIngredient){
        return itemsRepository.findByApiIngredient(apiIngredient);
    }
}



