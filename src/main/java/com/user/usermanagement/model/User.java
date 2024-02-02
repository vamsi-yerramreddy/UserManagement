package com.user.usermanagement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity

public class User extends  BaseModel{

    private String userName;
    private String emailId;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;


}
