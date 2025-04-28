package org.example.dto;

import org.example.entity.Role;

public class PermissionDTO {
    private String role;
    private String jwt;

    public PermissionDTO(Role r,String jwt){
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
}
