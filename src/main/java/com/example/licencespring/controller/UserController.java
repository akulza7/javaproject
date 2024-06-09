package com.example.licencespring.controller;

import com.example.licencespring.model.User;
import com.example.licencespring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public User addUser(@RequestParam String username, @RequestParam String password) {
        return userService.createUser(username, password);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        System.out.println(userService.getAllUsers());
        return userService.getAllUsers();
    }
}
