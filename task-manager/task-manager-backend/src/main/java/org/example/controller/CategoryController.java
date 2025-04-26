package org.example.controller;

import org.example.dto.CategoryDTO;
import org.example.entity.User;
import org.example.service.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // Konstruktor
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
                                      @RequestParam String name) {
        User user = getUserFromDetails(userDetails);
        var category = categoryService.createCategory(user, name);
        return new CategoryDTO(category.getId(), category.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long id) {
        User user = getUserFromDetails(userDetails);
        categoryService.deleteCategory(id, user);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        // Pobierz użytkownika na podstawie username
        return new User(); // TODO: Zaimplementuj odpowiednią metodę
    }
}

