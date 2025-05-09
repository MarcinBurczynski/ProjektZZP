package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskDTO;
import org.example.entity.User;
import org.example.service.TaskService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public List<TaskDTO> listTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserFromDetails(userDetails);
        return taskService.getTasksForUser(user);
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id) {
        User user = userService.getUserFromDetails(userDetails);
        return taskService.getTasksByIdAndUser(user,id);
    }

    @PostMapping
    public TaskDTO createTask(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody TaskDTO taskDTO) {
        User user = userService.getUserFromDetails(userDetails);
        return taskService.createTask(user, taskDTO);
    }

    @PutMapping("/{id}")
    public void updateTask(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable("id") Long id, @RequestBody TaskDTO taskDTO) {
        User user = userService.getUserFromDetails(userDetails);
        taskDTO.setId(id);
        taskService.updateTask(user, taskDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable("id") Long id) {
        User user = userService.getUserFromDetails(userDetails);
        taskService.deleteTask(user,id);
    }
}
