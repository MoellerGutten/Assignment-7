package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;
import com.example.accessing_data_rest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "", produces = "application/json")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping(value ="/searchusers", produces="application/json")
    public List<User> searchUsers(@RequestParam("name") String name) {
        return userService.searchUsers(name);
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public User postUser(@RequestBody User user) {
        return userService.createUser(user);
    }


}
