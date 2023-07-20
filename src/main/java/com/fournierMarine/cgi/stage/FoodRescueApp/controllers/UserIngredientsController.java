package com.fournierMarine.cgi.stage.FoodRescueApp.controllers;

import com.fournierMarine.cgi.stage.FoodRescueApp.RecipesApiConnection;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.ExpirationDateItem;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.ExpirationDateItemRepository;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.ItemsRepository;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.DAYS;


@Controller
public class UserIngredientsController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ExpirationDateItemRepository expirationDateItemRepository;

    private ArrayList<Map<String, String>> recipesListUser;

    @Autowired
    public UserIngredientsController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        recipesListUser = new ArrayList<>();
    }

    @GetMapping("/myIngredients")
    public String getMyIngredients(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            List<ExpirationDateItem> ListItems = usersRepository.findByEmail(loggedInUser).getListDateItems();
            Collections.sort(ListItems, Comparator.comparing(ExpirationDateItem::getDate, Comparator.nullsLast(Date::compareTo)));
            ArrayList<String> ingredientsUserList = new ArrayList<>();
            List<Map<String, String>> dateUserIngredient = new ArrayList<>();


            for (ExpirationDateItem item : ListItems) {
                System.out.println("for (ExpirationDateItem item : ListItems)--------------------------------------------------------------------");

                String ingredientUser = item.getItems().getApiIngredient();
                System.out.println("ingredientUser: " + ingredientUser);
                ingredientsUserList.add(ingredientUser);

                String date = item.getDate() != null ? item.getDate().toString() : null;
                System.out.println("date: " + item.getDate());

                Items itemId = item.getItems();
                System.out.println("itemId: " + itemId);

                Users userId = item.getUsers();
                System.out.println("userId: " + userId);

                Long dateItemId = item.getId();
                System.out.println("dateItemId: " + dateItemId);


                Map<String, String> itemUserMap = new HashMap<>();
                itemUserMap.put("date", date);
                itemUserMap.put("ingredient", itemId.getApiIngredient());
                itemUserMap.put("email", userId.getEmail());
                itemUserMap.put("dateItemId", dateItemId.toString());

                if (date != null) {
                    System.out.println("if (date != null)--------------------------------------------------------------------------------");
                    // Calculation of days counted
                    LocalDate currentDate = LocalDate.now();
                    LocalDate expirationDate = Instant.ofEpochMilli(item.getDate().getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    long daysRemaining = DAYS.between(currentDate, expirationDate);
                    itemUserMap.put("daysRemaining", String.valueOf(daysRemaining));
                }

                if (date == null){
                    String naDays = "none";
                    itemUserMap.put("daysRemaining", naDays);
                }

                dateUserIngredient.add(itemUserMap);

                System.out.println("dateUserIngredient------------------------------------------------------------------");
                System.out.println("dateUserIngredient size: " + dateUserIngredient.size());
            }

            recipesListUser = RecipesApiConnection.getRecipesList(ingredientsUserList);
            System.out.println(recipesListUser);


            System.out.println("dateUserIngredient: " + dateUserIngredient);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("ingredientsUserList", ingredientsUserList);
            model.addAttribute("recipesUser", recipesListUser);
            model.addAttribute("dateUserIngredient", dateUserIngredient);
        //    model.addAttribute("listDateItems", ListItems);

        }
        return "myIngredients";
    }

    @PostMapping("/myIngredients")
    @Transactional
    public String viewMyIngredients(
            @RequestParam("expirationDateString") String expirationDateString,
            @RequestParam("dateItemId") Long dateItemId,
            Model model,
            HttpSession session
    ) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);

            Users user = usersRepository.findByEmail(loggedInUser);
            if (user != null) {

                Items item = itemsRepository.findItemById(dateItemId);


                // Check if the item already has an expiration date
                ExpirationDateItem existingDateItem = expirationDateItemRepository.findById(dateItemId).orElse(null);

                if (existingDateItem != null && existingDateItem.getUsers().equals(user)) {

                    System.out.println("ENTREI IF EXISTINGDATEITEM---------------------------------");

                    existingDateItem.setDateFromString(expirationDateString);
                    expirationDateItemRepository.save(existingDateItem);
                } else {
                    System.out.println("ENTREI ELSE EXISTINGDATEITEM---------------------------------");
                    // Item does not have an expiration date or the provided dateItemId is invalid, create a new one
                    ExpirationDateItem newDateItem = new ExpirationDateItem();
                    newDateItem.setItems(item);
                    newDateItem.setDateFromString(expirationDateString);
                    newDateItem.setUsers(user);
                    expirationDateItemRepository.save(newDateItem);
                }

//                model.addAttribute("selectedIngredient", ingredient);
            }
        }
        return "redirect:/myIngredients";
    }


    @PostMapping("/removeIngredient")
    @Transactional
    public String removeIngredient(
            @RequestParam("ingredient") String ingredient,
            @RequestParam("dateItemId") Long dateItemId,
            Model model,
            HttpSession session
    ) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);

            Users user = usersRepository.findByEmail(loggedInUser);
            if (user != null) {
                Items item = itemsRepository.findByApiIngredient(ingredient);

                // Find the ExpirationDateItem to remove
                ExpirationDateItem expirationDateItem = expirationDateItemRepository.findById(dateItemId).orElse(null);

                // Check if the ExpirationDateItem exists and is associated with the user
                if (expirationDateItem != null && expirationDateItem.getUsers().equals(user)) {
                    // Remove the ExpirationDateItem from the user
                    user.removeExpirationDateItem(expirationDateItem);
                    usersRepository.save(user);
                }
            }
        }

        return "redirect:/myIngredients";
    }


}

