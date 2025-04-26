package org.example.controller;

import org.example.dto.CategoryDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;
    // Konstruktor
    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public List<CategoryDTO> listCategories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return categoryService.getCategoriesForUser(user)
                .stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public CategoryDTO createCategory(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam("name") String name) {
        User user = getUserFromDetails(userDetails);
        var category = categoryService.createCategory(user, name);
        return new CategoryDTO(category.getId(), category.getName());
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id) {
        User user = getUserFromDetails(userDetails);
        return categoryService.getCategoryByIdAndUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id) {
        User user = getUserFromDetails(userDetails);
        categoryService.deleteCategory(id, user);
    }

    private User getUserFromDetails(UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }

}

