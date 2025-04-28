package org.example.dto;

import org.example.entity.Category;

public class CategoryDTO {
    private Long id;
    private String name;
    private Long userId;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, Long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public CategoryDTO(Category c){
        this.id = c.getId();
        this.name = c.getName();
        this.userId = c.getUser().getId();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
