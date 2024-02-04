package com.user.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class PasswordResetToken extends  BaseModel{
    private String token;
    @OneToOne
    private User user;
    private Date expiryDate;
}
