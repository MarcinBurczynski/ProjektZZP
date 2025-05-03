package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.TaskDTO;
import org.example.entity.Category;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, CategoryService categoryService, UserService userService) {
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    public List<TaskDTO> getTasksForUser(User user) {
        List<Task> tasks;

        if(user.getRole().ordinal()>1){
            tasks = taskRepository.findAllByUser(user);
        }else {
            tasks = taskRepository.findAll();
        }

        return tasks
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
    }

    public TaskDTO getTasksByIdAndUser(User user, Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(user.getId()) && user.getRole().ordinal()>1) {
            throw new SecurityException("You don't have access to this task");
        }

        return new TaskDTO(task);
    }

    @Transactional
    public TaskDTO createTask(User user, TaskDTO taskDTO) {
        if(taskDTO.getUserId()==null){
            taskDTO.setUserId(user.getId());
        }

        if(!taskDTO.getUserId().equals(user.getId())){
            if(user.getRole().ordinal()>1){
                throw new SecurityException("You can't create tasks for other users!!!");
            }else{
                user = userService.getUserObjectById(user, taskDTO.getUserId());
            }
        }

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setCategory(categoryService.getCategoryObjectByIdAndUser(user,taskDTO.getCategoryId()));
        task.setUser(user);

        task = taskRepository.save(task);

        return new TaskDTO(task);
    }

    @Transactional
    public void updateTask(User user, TaskDTO taskDTO) {
        if(taskDTO.getUserId()==null){
            taskDTO.setUserId(user.getId());
        }

        if(!taskDTO.getUserId().equals(user.getId())){
            if(user.getRole().ordinal()>1){
                throw new SecurityException("You can't update tasks for other users!!!");
            }else{
                user = userService.getUserObjectById(user, taskDTO.getUserId());
            }
        }

        Task task = taskRepository.findByIdAndUser(taskDTO.getId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setCategory(categoryService.getCategoryObjectByIdAndUser(user,taskDTO.getCategoryId()));
        if(taskDTO.getUserId()!=null)task.setUser(user);

        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(user.getId()) && user.getRole().ordinal()>1) {
            throw new SecurityException("You can't delete tasks of other users");
        }

        taskRepository.delete(task);
    }
}

