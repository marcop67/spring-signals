package com.copytrading.bot.controller;

import com.copytrading.bot.dto.UserDto;
import com.copytrading.bot.logging.LoggingService;
import com.copytrading.bot.model.User;
import com.copytrading.bot.repository.UserRepository;
import com.copytrading.bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    LoggingService logService;

    @GetMapping("")
    public List<User> list() {
        return userService.listAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable String id) {
        try {
            User user = userService.getUser(id.trim());
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/")
    public User add(@RequestBody User user) {
        try {
            user.setId(user.getId().trim());
            User existUser = userService.getUser(user.getId());
            existUser.setIs_active(false);
            return userService.saveUser(existUser);
        } catch (NoSuchElementException e) {
            return userService.saveUser(user);
        }

    }
    @PutMapping("/")
    public User update(@RequestBody UserDto user) {
        try {
            user.setId(user.getId().trim());
            User existUser = userService.getUser(user.getId());
            if(existUser == null){
                return null;
            }else{
                if(user.getNew_status()!= null){
                    if(user.getNew_status().equals("active") || user.getNew_status().equals("pending-cancel")){
                        existUser.setIs_active(true);
                    }else{
                        existUser.setIs_active(false);
                        logService.log(user.getNew_status());
                    }
                }
            }

            return userService.saveUser(existUser);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {

        userService.deleteUser(id.trim());
    }
}