package org.example.controller;

import org.example.dto.CategoryDTO;
import org.example.entity.User;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public List<CategoryDTO> listCategories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserFromDetails(userDetails);
        return categoryService.getCategoriesForUser(user);
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id) {
        User user = userService.getUserFromDetails(userDetails);
        return categoryService.getCategoryByIdAndUser(user, id);
    }

    @PostMapping
    public void createCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestBody CategoryDTO categoryDTO) {
        User user = userService.getUserFromDetails(userDetails);
        categoryService.createCategory(user,categoryDTO);
    }

    @PutMapping("/{id}")
    public void updateCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id,
                               @RequestBody CategoryDTO categoryDTO){
        User user = userService.getUserFromDetails(userDetails);
        categoryDTO.setId(id);
        categoryService.updateCategory(user,categoryDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable("id") Long id) {
        User user = userService.getUserFromDetails(userDetails);
        categoryService.deleteCategory(user, id);
    }

}

