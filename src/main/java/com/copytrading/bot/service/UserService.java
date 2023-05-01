package com.copytrading.bot.service;

import com.copytrading.bot.model.User;
import com.copytrading.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> listAllUser() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(String id) {
        return userRepository.findById(id).get();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
