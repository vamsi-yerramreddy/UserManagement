package com.user.usermanagement.dto;

import lombok.Data;

@Data
public class ResponseDto {
    private ErrorResponseDto error;
    private UserDto userDto;
}
