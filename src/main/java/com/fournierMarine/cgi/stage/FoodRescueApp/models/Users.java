package com.fournierMarine.cgi.stage.FoodRescueApp.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpirationDateItem> listDateItems;

    public Users() {
        // Default constructor
    }

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ExpirationDateItem> getListDateItems() {
        return listDateItems;
    }

    public void setListDateItems(List<ExpirationDateItem> listDateItems) {
        this.listDateItems = listDateItems;
    }

    public void removeExpirationDateItem(ExpirationDateItem expirationDateItem) {
        if (listDateItems != null) {
            listDateItems.remove(expirationDateItem);
            expirationDateItem.setUsers(null);
        }
    }

    @Override
    public String toString() {
        return "Users (" + "id: " + id + ", username: " + username + ", email: " + email + ", password: " + password + ')';
    }
}
