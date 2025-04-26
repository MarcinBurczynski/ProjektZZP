package org.example.controller;

import org.example.dto.TaskDTO;
import org.example.entity.User;
import org.example.service.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> listTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return taskService.getTasksForUser(user);
    }

    @PostMapping
    public TaskDTO createTask(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TaskDTO taskDTO) {
        User user = getUserFromDetails(userDetails);
        return taskService.createTask(user, taskDTO);
    }

    @PutMapping("/{id}")
    public void updateTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        User user = getUserFromDetails(userDetails);
        taskService.updateTask(id, user, taskDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User user = getUserFromDetails(userDetails);
        taskService.deleteTask(id, user);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        // W tym miejscu musisz pobrać użytkownika na podstawie username
        return new User(); // Placeholder do pobierania użytkownika
    }
}
