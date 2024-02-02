package com.user.usermanagement.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.sql.Time;
@Entity
@Data
public class Session extends  BaseModel{

    private String token;
    private Time expiryTime;
    private User user;
    private boolean active;

}
