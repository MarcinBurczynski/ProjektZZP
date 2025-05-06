package org.example.service;

import org.example.dto.CategoryDTO;
import org.example.entity.Category;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);
    }

    @Test
    void createCategory_asUser_createsOwnCategory() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(1L);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");

        when(categoryRepository.findByUserAndName(user, "Books")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        CategoryDTO result = categoryService.createCategory(user, dto);

        assertEquals("Books", result.getName());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void createCategory_asUser_createCategoryForOtherUser_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(1L);

        User otherUser = new User("otherUser", "", "other@example.com", Role.USER);
        otherUser.setId(2L);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");
        dto.setUserId(2L);

        assertThrows(SecurityException.class, () -> categoryService.createCategory(user, dto),
                "You can't create categories for other users!!!");
    }

    @Test
    void createCategory_categoryAlreadyExists_throwsIllegalArgumentException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(1L);

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");

        when(categoryRepository.findByUserAndName(user, "Books")).thenReturn(Optional.of(new Category("Books", user)));

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(user, dto),
                "Category with this name already exists for given user!");
    }
}
