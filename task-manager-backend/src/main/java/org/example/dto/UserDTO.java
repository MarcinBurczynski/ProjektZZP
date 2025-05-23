package org.example.dto;

import org.example.entity.Role;
import org.example.entity.User;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;

    public UserDTO() {
    }

    public UserDTO(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.role = u.getRole().toString();
        this.email = u.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole(){
        return Role.valueOf(role);
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(String role) { this.role = role;}
}
