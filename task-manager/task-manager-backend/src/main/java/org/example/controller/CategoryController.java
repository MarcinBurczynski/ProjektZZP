package org.example.controller;

import org.example.entity.Category;
import org.example.entity.User;
import org.example.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

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
        // Tutaj powinieneś pobierać użytkownika np. po username z bazy
        return new User(); // TEMP: Tu musisz sobie dopiąć pobieranie usera na serio
    }
}

