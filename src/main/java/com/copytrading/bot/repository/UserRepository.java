package com.copytrading.bot.repository;

import com.copytrading.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.bot = true AND u.is_active = true")
    List<User> findActiveBotUsers();

//    @Query("SELECT u FROM User u WHERE u.bot = true AND u.is_active = true AND u.premium = TRUE")
//    List<User> findActivePremiumBotUsers();
//
//
//    @Query("SELECT u FROM User u WHERE u.bot = true AND u.is_active = true AND u.base = TRUE")
//    List<User> findActiveBasicBotUsers();

}
