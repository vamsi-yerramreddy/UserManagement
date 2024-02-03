package com.user.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class Session extends  BaseModel{

    private String token;
    private Date expiryTime;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.ORDINAL)   // ORDINAL is used to store the enum value as integer in the database
    private SessionStatus status;


    /*private String ipAddress;
    private String userAgent;
    private String deviceType;
    private String deviceName;
    private String deviceOs;
    private String deviceOsVersion;
    private String deviceBrowser;
    private String deviceBrowserVersion;*/



}
