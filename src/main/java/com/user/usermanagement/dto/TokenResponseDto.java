package com.user.usermanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String token;
    private String message;

    public TokenResponseDto(String message){
        this.message = message;
    }

}
