package org.example.service;

import org.example.entity.Category;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.entity.Status;
import org.example.dto.TaskDTO;
import org.example.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getTasksForUser(User user) {
        return taskRepository.findAllByUser(user)
                .stream()
                .map(task -> new TaskDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().name(),
                        task.getCategory().getId(),
                        task.getUser().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(User user, TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(Status.valueOf(taskDTO.getStatus()));
        task.setCategory(new Category());
        task.setUser(user);

        task = taskRepository.save(task);

        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus().name(), task.getCategory().getId(), task.getUser().getId());
    }

    @Transactional
    public void updateTask(Long taskId, User user, TaskDTO taskDTO) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(Status.valueOf(taskDTO.getStatus()));
        task.setCategory(new Category());

        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        taskRepository.delete(task);
    }
}

