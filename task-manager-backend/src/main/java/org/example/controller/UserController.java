package org.example.controller;

import org.example.dto.UserDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getUsers(@AuthenticationPrincipal UserDetails userDetails) {
        User auth = userService.getUserFromDetails(userDetails);
        return userService.getUsers(auth);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable("id") Long id) {
        User auth = userService.getUserFromDetails(userDetails);
        return userService.getUserById(auth,id);
    }

    @PostMapping
    public void createUser(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestBody UserOperationDTO userOperationDTO) {
        User auth = userService.getUserFromDetails(userDetails);
        userService.createUser(auth,userOperationDTO);
    }

    @PutMapping("/{id}")
    public void updateUser(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable("id") Long id,
                           @RequestBody UserOperationDTO userOperationDTO) {
        User auth = userService.getUserFromDetails(userDetails);
        userOperationDTO.setId(id);
        userService.updateUser(auth,userOperationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable("id") Long id) {
        User auth = userService.getUserFromDetails(userDetails);
        userService.deleteUser(auth,id);
    }
}
