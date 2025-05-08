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

    @Test
    void getCategoriesForUser_userSeesOwnCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Sport");

        when(categoryRepository.findByUserAndName(user, "Sport")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(200L);
            return c;
        });

        categoryService.createCategory(user, dto);

        Category savedCategory = new Category("Sport", user);
        savedCategory.setId(200L);
        when(categoryRepository.findAllByUser(user)).thenReturn(List.of(savedCategory));

        List<CategoryDTO> categories = categoryService.getCategoriesForUser(user);

        assertEquals(1, categories.size());
        assertEquals("Sport", categories.get(0).getName());
        assertEquals(user.getId(), categories.get(0).getUserId());
    }

    @Test
    void getCategoriesForUser_adminSeesAllCategory() {
        CategoryDTO dto1 = new CategoryDTO();
        CategoryDTO dto2 = new CategoryDTO();
        dto1.setName("Sport");
        dto2.setName("Travel");

        when(categoryRepository.findByUserAndName(user, "Sport")).thenReturn(Optional.empty());
        when(categoryRepository.findByUserAndName(admin, "Travel")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            if (c.getName().equals("Sport")) {
                c.setId(200L);
            } else if (c.getName().equals("Travel")) {
                c.setId(201L);
            }
            return c;
        });

        categoryService.createCategory(user, dto1);
        categoryService.createCategory(admin, dto2);

        Category savedCategory1 = new Category("Sport", user);
        Category savedCategory2 = new Category("Travel", admin);
        when(categoryRepository.findAll()).thenReturn(List.of(savedCategory1,savedCategory2));

        List<CategoryDTO> categories = categoryService.getCategoriesForUser(admin);

        assertEquals(2, categories.size());
        assertEquals("Sport", categories.get(0).getName());
        assertEquals(user.getId(), categories.get(0).getUserId());
        assertEquals("Travel", categories.get(1).getName());
        assertEquals(admin.getId(), categories.get(1).getUserId());
    }

    @Test
    void getCategoryByIdAndUser_returnsCategoryDTO() {
        Category savedCategory = new Category("Food", user);
        savedCategory.setId(300L);

        when(categoryRepository.findById(300L)).thenReturn(Optional.of(savedCategory));

        CategoryDTO result = categoryService.getCategoryByIdAndUser(user, 300L);

        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals(300L, result.getId());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void getCategoryObjectByIdAndUser_userOwnsCategory_returnsCategory() {
        Category category = new Category("Travel", user);
        category.setId(400L);

        when(categoryRepository.findById(400L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryObjectByIdAndUser(user, 400L);

        assertNotNull(result);
        assertEquals("Travel", result.getName());
        assertEquals(400L, result.getId());
    }

    @Test
    void getCategoryObjectByIdAndUser_otherUserCategory_throwsSecurityException() {
        User other = new User("someone", "", "some@example.com", Role.USER);
        other.setId(99L);

        Category category = new Category("Private", other);
        category.setId(777L);

        when(categoryRepository.findById(777L)).thenReturn(Optional.of(category));

        assertThrows(SecurityException.class, () -> {
            categoryService.getCategoryObjectByIdAndUser(user, 777L);
        });
    }

    @Test
    void updateCategory_asOwner_updatesSuccessfully() {
        Category existing = new Category("OldName", user);
        existing.setId(10L);

        CategoryDTO dto = new CategoryDTO(existing);
        dto.setName("NewName");

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByUserIdAndName(user.getId(), "NewName")).thenReturn(Optional.empty());

        categoryService.updateCategory(user, dto);

        verify(categoryRepository).save(argThat(cat ->
                cat.getId().equals(10L) &&
                        cat.getName().equals("NewName") &&
                        cat.getUser().equals(user)
        ));
    }

    @Test
    void updateCategory_nameAlreadyExists_throwsException() {
        Category existing = new Category("OldName", user);
        existing.setId(11L);

        Category other = new Category("NewName", user);
        other.setId(12L);

        CategoryDTO dto = new CategoryDTO(existing);
        dto.setName("NewName");

        when(categoryRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByUserIdAndName(user.getId(), "NewName")).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(user, dto));
    }

    @Test
    void updateCategory_userTriesToUpdateOtherUsersCategory_throwsSecurityException() {
        User other = new User("other", "", "other@example.com", Role.USER);
        other.setId(99L);

        CategoryDTO dto = new CategoryDTO();
        dto.setId(13L);
        dto.setName("UpdatedName");
        dto.setUserId(99L);

        Category existing = new Category("OldName", other);
        existing.setId(13L);

        when(categoryRepository.findById(13L)).thenReturn(Optional.of(existing));

        assertThrows(SecurityException.class, () -> categoryService.updateCategory(user, dto));
    }

    @Test
    void updateCategory_userIdIsNull_setsUserIdAndUpdatesSuccessfully() {
        Category existing = new Category("OldName", user);
        existing.setId(14L);

        CategoryDTO dto = new CategoryDTO(existing);
        dto.setName("NewName");
        dto.setUserId(null);

        when(categoryRepository.findById(14L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByUserIdAndName(user.getId(), "NewName")).thenReturn(Optional.empty());

        categoryService.updateCategory(user, dto);

        verify(categoryRepository).save(argThat(cat ->
                cat.getId().equals(14L) &&
                        cat.getName().equals("NewName") &&
                        cat.getUser().equals(user)
        ));
    }

    @Test
    void deleteCategory_asOwner_deletesSuccessfully() {
        Category category = new Category("ToDelete", user);
        category.setId(20L);

        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(user, 20L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_otherUser_throwsSecurityException() {
        User other = new User("other", "", "other@example.com", Role.USER);
        other.setId(3L);

        Category category = new Category("Private", other);
        category.setId(21L);

        when(categoryRepository.findById(21L)).thenReturn(Optional.of(category));

        assertThrows(SecurityException.class, () -> categoryService.deleteCategory(user, 21L));
    }

    @Test
    void deleteCategory_notFound_throwsIllegalArgumentException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(user, 999L));
    }
}
