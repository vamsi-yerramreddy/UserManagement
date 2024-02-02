package com.user.usermanagement.dto;

import com.user.usermanagement.model.Role;
import com.user.usermanagement.model.User;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String emailId;
    private Set<Role> roles;

    public static UserDto from (User savedUser){
        UserDto userDto = new UserDto();
        userDto.setEmailId(savedUser.getEmailId());
       // userDto.setRoles(savedUser.getRoles());
        return userDto;

    }
}
