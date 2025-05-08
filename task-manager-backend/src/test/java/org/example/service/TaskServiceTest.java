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
    void getTasksByIdAndUser_shouldThrowSecurityExceptionWhenAccessingOthersTask() {
        User requester = createUser(1L, Role.USER);

        User owner = createUser(2L, Role.USER);
        Task task = createTask(1L, owner);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            taskService.getTasksByIdAndUser(requester, 1L);
        });

        assertEquals("You don't have access to this task", exception.getMessage());
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
    void createTask_asAdmin_createsTaskForOtherUser() {
        User admin = createUser(1L, Role.ADMIN);
        Long targetUserId = 2L;

        TaskDTO dto = new TaskDTO(null, "AdminTask", "Desc", "NEW", 1L, targetUserId);

        User otherUser = createUser(targetUserId, Role.USER);
        Category category = new Category();
        category.setId(1L);

        when(userService.getUserObjectById(admin, targetUserId)).thenReturn(otherUser);
        when(categoryService.getCategoryObjectByIdAndUser(otherUser, 1L)).thenReturn(category);
        when(taskRepository.save(any())).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(10L);
            return task;
        });

        TaskDTO result = taskService.createTask(admin, dto);

        assertEquals("AdminTask", result.getTitle());
        assertEquals(targetUserId, result.getUserId());
        verify(userService).getUserObjectById(admin, targetUserId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_shouldSetUserIdToCurrentUserIfNull() {
        User user = createUser(1L, Role.USER);

        TaskDTO dto = new TaskDTO(null, "Test", "Test", "NEW", 1L, null);

        Category category = new Category();
        category.setId(1L);

        when(categoryService.getCategoryObjectByIdAndUser(user, 1L)).thenReturn(category);
        when(taskRepository.save(any())).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskDTO result = taskService.createTask(user, dto);

        assertEquals(user.getId(), result.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowSecurityException_whenUserIsNotAdminAndTriesToCreateTaskForAnotherUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setUserId(2L);

        assertThrows(SecurityException.class, () -> taskService.createTask(user, taskDTO));
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
    void updateTask_shouldSetUserIdToCurrentUserIfNull() {
        User user = createUser(1L, Role.USER);

        TaskDTO dto = new TaskDTO(1L, "Updated Title", "Updated Description", "IN_PROGRESS", null, null); // userId is null

        Category category = new Category();
        category.setId(1L);

        when(categoryService.getCategoryObjectByIdAndUser(user, 1L)).thenReturn(category);
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(new Task()));
        when(taskRepository.save(any())).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        taskService.updateTask(user, dto);

        assertEquals(user.getId(), dto.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_asAdmin_shouldUpdateTaskForOtherUser() {
        User admin = createUser(1L, Role.ADMIN);
        Long targetUserId = 2L;

        TaskDTO dto = new TaskDTO(1L, "Admin Updated", "Updated Desc", "IN_PROGRESS", 1L, targetUserId);

        User otherUser = createUser(targetUserId, Role.USER);
        Category category = new Category();
        category.setId(1L);

        Task existingTask = createTask(1L, otherUser);
        existingTask.setCategory(category);

        when(userService.getUserObjectById(admin, targetUserId)).thenReturn(otherUser);
        when(taskRepository.findByIdAndUser(1L, otherUser)).thenReturn(Optional.of(existingTask));
        when(categoryService.getCategoryObjectByIdAndUser(otherUser, 1L)).thenReturn(category);

        taskService.updateTask(admin, dto);

        assertEquals("Admin Updated", existingTask.getTitle());
        assertEquals("Updated Desc", existingTask.getDescription());
        assertEquals(otherUser, existingTask.getUser());

        verify(userService).getUserObjectById(admin, targetUserId);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldThrowSecurityException_whenUserIsNotAdminAndTriesToUpdateOtherUsersTask() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(100L);
        taskDTO.setUserId(2L);

        assertThrows(SecurityException.class, () -> taskService.updateTask(user, taskDTO));
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
