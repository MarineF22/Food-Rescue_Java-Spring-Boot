package com.fournierMarine.cgi.stage.FoodRescueApp.repository;

import com.fournierMarine.cgi.stage.FoodRescueApp.models.ExpirationDateItem;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExpirationDateItemRepository extends JpaRepository<ExpirationDateItem, Long> {

    ExpirationDateItem findByUsersAndItemsAndId(Users user, Items item, Long id);

    void deleteByUsersAndItems(Users user, Items item);

    void deleteById(Long id);
}
