package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PermissionDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.Role;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class PublicUserController {

    private final UserService userService;

    @Autowired
    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<PermissionDTO> login(@RequestBody UserOperationDTO userOperationDTO) {
        PermissionDTO perm = userService.login(userOperationDTO);
        return ResponseEntity.ok(perm);
    }

    @PostMapping("/register")
    public ResponseEntity<PermissionDTO> register(@RequestBody UserOperationDTO userOperationDTO) {
        userOperationDTO.setRole(Role.USER);
        PermissionDTO perm = userService.register(userOperationDTO);
        return ResponseEntity.ok(perm);
    }
}