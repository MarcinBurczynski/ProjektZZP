package org.example.controller;

import org.example.entity.Category;
import org.example.entity.User;
import org.example.service.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // KONSTRUKTOR
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> listCategories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails); // metoda pomocnicza
        return categoryService.getCategoriesForUser(user);
    }

    @PostMapping
    public Category createCategory(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam String name) {
        User user = getUserFromDetails(userDetails);
        return categoryService.createCategory(user, name);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long id) {
        User user = getUserFromDetails(userDetails);
        categoryService.deleteCategory(id, user);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        // TODO: W prawdziwym projekcie tutaj pobierasz User po username
        return new User(); // TEMP: Tu musisz podmienić na prawdziwe pobieranie usera z bazy
    }
}

