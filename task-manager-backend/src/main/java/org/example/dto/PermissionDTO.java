package org.example.dto;

import org.example.entity.Role;

public class PermissionDTO {
    private Long userId;
    private String role;
    private String jwt;

    public PermissionDTO(Long userId, Role r,String jwt){
        this.userId = userId;
        this.role = r.toString();
        this.jwt = jwt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role.toString();
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
