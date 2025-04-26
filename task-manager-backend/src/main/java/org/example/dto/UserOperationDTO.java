package org.example.dto;

import org.example.entity.User;

public class UserOperationDTO {
    private String username;
    private String password;

    public UserOperationDTO() {
    }

    public UserOperationDTO(User u) {
        this.username = u.getUsername();
        this.password = u.getPassword();
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
}
