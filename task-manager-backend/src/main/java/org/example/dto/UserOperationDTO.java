package org.example.dto;

import org.example.entity.Role;
import org.example.entity.User;

public class UserOperationDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;

    public UserOperationDTO() {
    }

    public UserOperationDTO(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.role = u.getRole().toString();
        this.email = u.getEmail();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return Role.valueOf(role);
    }

    public void setRole(Role role) {
        this.role = role.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
