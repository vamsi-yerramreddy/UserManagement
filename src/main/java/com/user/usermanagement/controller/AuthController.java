package com.user.usermanagement.controller;

import com.user.usermanagement.dto.*;
import com.user.usermanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/validate")
    public ResponseEntity<TokenResponseDto> validate(@RequestBody ValidateTokenRequestDto validateTokenRequestDto){

     return authService.validate(validateTokenRequestDto.getToken(), validateTokenRequestDto.getUserId());

    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> passwordReset(@RequestParam @NonNull String  emailId){
     return    authService.resetPassword(emailId);
    }
    @PostMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetPassword(@RequestBody ValidateTokenRequestDto validateTokenRequestDto){
        return authService.resetPasswordConfirm(validateTokenRequestDto.getToken(), validateTokenRequestDto.getEmailId());
    }


}
