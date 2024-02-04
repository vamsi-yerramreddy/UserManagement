package com.user.usermanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequestDto {
    private String token;
    private Long userId;
    private String emailId;

}
