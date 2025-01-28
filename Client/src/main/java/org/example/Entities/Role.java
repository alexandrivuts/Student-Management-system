package org.example.Entities;

import jakarta.persistence.*;

import java.io.Serializable;


public class Role implements Serializable{

    private int role_id;

    private String roleName;

    public int getRole_id() {
        return role_id;
    }
    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
