package com.fournierMarine.cgi.stage.FoodRescueApp.repository;

import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);

    Users findByEmail(String email);

    void delete(Users user);
}