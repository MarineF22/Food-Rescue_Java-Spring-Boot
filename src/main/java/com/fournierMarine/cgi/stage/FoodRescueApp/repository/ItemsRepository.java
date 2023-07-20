package com.fournierMarine.cgi.stage.FoodRescueApp.repository;

import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface ItemsRepository extends JpaRepository<Items, Long> {
    Items findByApiIngredient(String apiIngredient);

    ArrayList<Items> findByIngredientContainingIgnoreCase(String ingredient);

    Items findItemById(Long dateItemId);
}
