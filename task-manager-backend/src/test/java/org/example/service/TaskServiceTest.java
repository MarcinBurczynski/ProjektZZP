package org.example.service;

import org.example.dto.TaskDTO;
import org.example.entity.*;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private Task createTask(Long id, User user) {
        Task task = new Task();
        task.setId(id);
        task.setTitle("Test");
        task.setDescription("Test desc");
        task.setStatus(Status.NEW);
        task.setUser(user);
        Category category = new Category();
        category.setId(1L);
        task.setCategory(category);
        return task;
    }

    @Test
    void getTasksForUser_shouldReturnAllTasksForAdmin() {
        User user = createUser(1L, Role.ADMIN);
        List<Task> tasks = List.of(createTask(1L, user));
        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskDTO> result = taskService.getTasksForUser(user);

        assertEquals(1, result.size());
        verify(taskRepository).findAll();
    }

    @Test
    void getTasksForUser_shouldReturnOwnTasksForRegularUser() {
        User user = createUser(1L, Role.USER);
        List<Task> tasks = List.of(createTask(1L, user));
        when(taskRepository.findAllByUser(user)).thenReturn(tasks);

        List<TaskDTO> result = taskService.getTasksForUser(user);

        assertEquals(1, result.size());
        verify(taskRepository).findAllByUser(user);
    }

    @Test
    void getTasksByIdAndUser_shouldReturnTask() {
        User user = createUser(1L, Role.USER);
        Task task = createTask(1L, user);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTasksByIdAndUser(user, 1L);

        assertEquals(task.getId(), result.getId());
    }

    @Test
    void createTask_shouldCreateTaskForSelf() {
        User user = createUser(1L, Role.USER);
        TaskDTO dto = new TaskDTO(null, "Test", "Test", "NEW", 1L, 1L);
        Category category = new Category();
        category.setId(1L);

        when(categoryService.getCategoryObjectByIdAndUser(user, 1L)).thenReturn(category);
        when(taskRepository.save(any())).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskDTO result = taskService.createTask(user, dto);

        assertEquals("Test", result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_shouldUpdateTaskForSelf() {
        User user = createUser(1L, Role.USER);
        Task task = createTask(1L, user);
        TaskDTO dto = new TaskDTO(1L, "Updated", "Desc", "IN_PROGRESS", 1L, 1L);

        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));
        when(categoryService.getCategoryObjectByIdAndUser(user, 1L)).thenReturn(task.getCategory());

        taskService.updateTask(user, dto);

        assertEquals("Updated", task.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_shouldDeleteOwnTask() {
        User user = createUser(1L, Role.USER);
        Task task = createTask(1L, user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(user, 1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_shouldThrowSecurityExceptionIfNotOwner() {
        User user = createUser(1L, Role.USER);
        User other = createUser(2L, Role.USER);
        Task task = createTask(1L, other);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(SecurityException.class, () -> taskService.deleteTask(user, 1L));
    }
}
