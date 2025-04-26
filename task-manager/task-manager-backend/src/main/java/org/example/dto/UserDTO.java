package org.example.dto;

import org.example.entity.User;

public class UserDTO {
    private Long id;
    private String username;

    public UserDTO(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
