package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // Additional endpoints for login, etc.

}
