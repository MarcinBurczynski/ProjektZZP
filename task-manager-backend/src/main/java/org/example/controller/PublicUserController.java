package org.example.controller;

import org.example.dto.UserDTO;
import org.example.dto.UserOperationDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PublicUserController {

    private final UserService userService;

    @Autowired
    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserOperationDTO userOperationDTO) {
        String token = userService.login(userOperationDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserOperationDTO userOperationDTO) {
        userService.register(userOperationDTO);
        return ResponseEntity.ok().build();
    }
}