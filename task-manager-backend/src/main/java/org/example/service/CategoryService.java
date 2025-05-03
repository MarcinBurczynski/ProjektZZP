package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.CategoryDTO;
import org.example.entity.Category;
import org.example.entity.User;
import org.example.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryDTO> getCategoriesForUser(User user) {
        List<Category> categories;

        if(user.getRole().ordinal()>1){
            categories = categoryRepository.findAllByUser(user);
        }else{
            categories = categoryRepository.findAll();
        }

        return categories
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryByIdAndUser(User user, Long categoryId) {
        return new CategoryDTO(getCategoryObjectByIdAndUser(user,categoryId));
    }

    public Category getCategoryObjectByIdAndUser(User user, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getUser().getId().equals(user.getId()) && user.getRole().ordinal()>1) {
            throw new SecurityException("You don't have access to this category");
        }

        return category;
    }

    @Transactional
    public CategoryDTO createCategory(User user,CategoryDTO categoryDTO) {
        if(categoryDTO.getUserId()==null){
            categoryDTO.setUserId(user.getId());
        }

        if(!categoryDTO.getUserId().equals(user.getId())){
            if(user.getRole().ordinal()>1){
                throw new SecurityException("You can't create categories for other users!!!");
            }else{
                user = userService.getUserObjectById(user, categoryDTO.getUserId());
            }
        }

        categoryRepository.findByUserAndName(user, categoryDTO.getName()).ifPresent(c -> {
            throw new IllegalArgumentException("Category with this name already exists for given user!");
        });

        Category category = new Category(categoryDTO.getName(),user);
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional
    public void updateCategory(User user, CategoryDTO categoryDTO) {
        if(categoryDTO.getUserId()==null){
            categoryDTO.setUserId(user.getId());
        }

        if(!categoryDTO.getUserId().equals(user.getId())) {
            if (user.getRole().ordinal() > 1) {
                throw new SecurityException("You can't update categories for other users!!!");
            } else {
                user = userService.getUserObjectById(user, categoryDTO.getUserId());
            }
        }

        Category category = categoryRepository.findById(categoryDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        categoryRepository.findByUserIdAndName(categoryDTO.getUserId(), categoryDTO.getName()).ifPresent(c -> {
            if (!c.getId().equals(categoryDTO.getId())) {
                throw new IllegalArgumentException("Category with this name already exists for user!");
            }
        });

        category.setName(categoryDTO.getName());
        if(categoryDTO.getUserId()!=null)category.setUser(user);
        categoryRepository.save(category);
    }


    @Transactional
    public void deleteCategory(User user, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getUser().getId().equals(user.getId()) && user.getRole().ordinal()>1) {
            throw new SecurityException("You can't delete categories of other users");
        }

        categoryRepository.delete(category);
    }
}

