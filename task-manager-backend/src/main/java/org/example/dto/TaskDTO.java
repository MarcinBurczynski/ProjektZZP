package org.example.dto;

import org.example.entity.Status;
import org.example.entity.Task;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private Long categoryId;
    private Long userId;

    public TaskDTO() {}

    public TaskDTO(Long id, String title, String description, String status, Long categoryId, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public TaskDTO(Task t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.description = t.getDescription();
        this.status = t.getStatus().toString();
        this.categoryId = t.getCategory().getId();
        this.userId = t.getUser().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status.toString();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
