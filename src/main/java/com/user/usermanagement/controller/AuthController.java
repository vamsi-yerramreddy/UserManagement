package com.user.usermanagement.controller;

import com.user.usermanagement.dto.LoginRequestDto;
import com.user.usermanagement.dto.ResponseDto;
import com.user.usermanagement.dto.SignUpRequestDto;
import com.user.usermanagement.dto.UserDto;
import com.user.usermanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return authService.login(loginRequestDto.getEmailId(), loginRequestDto.getPassword());
    }
        @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto){
        return authService.signup(signUpRequestDto.getEmailId(),
                signUpRequestDto.getPassword());
    }

}
