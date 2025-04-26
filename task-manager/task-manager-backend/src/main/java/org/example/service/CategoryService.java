package org.example.service;

import org.example.entity.Category;
import org.example.entity.User;
import org.example.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findAllByUser(user);
    }

    @Transactional
    public Category createCategory(User user, String name) {
        categoryRepository.findByUserAndName(user, name).ifPresent(c -> {
            throw new IllegalArgumentException("Category with this name already exists for user!");
        });

        Category category = new Category();
        category.setName(name);
        category.setUser(user);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId, User user) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You can't delete this category");
        }
        categoryRepository.delete(category);
    }
}

