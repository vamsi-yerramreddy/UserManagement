package com.user.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name="users_vamsi")
public class User extends  BaseModel{

    private String userName;
    private String emailId;
    private String password;



}
