package com.user.usermanagement.service;

import com.user.usermanagement.controller.AuthController;
import com.user.usermanagement.dto.ErrorResponseDto;
import com.user.usermanagement.dto.ResponseDto;
import com.user.usermanagement.dto.UserDto;
import com.user.usermanagement.model.User;
import com.user.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AuthService {

    private UserRepository userRepository;

    @Autowired
    public AuthService( UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResponseDto> login (String email, String password){
        Optional<User> user=  userRepository.findUserByEmailId(email);

        if(user.isEmpty()){
            ErrorResponseDto error = new ErrorResponseDto("User not found");
            ResponseDto responseDto = new ResponseDto();
            responseDto.setUserDto(null);
            responseDto.setError(error);

            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }
        ResponseDto responseDto = new ResponseDto();
        if(user.get().getPassword().equals(password)){
            responseDto.setUserDto(UserDto.from(user.get()));
            responseDto.setError(null);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }else{
            responseDto.setError(new ErrorResponseDto("Password is incorrect"));
            responseDto.setUserDto(null);
            return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<ResponseDto> signup(String email, String password){
        Optional<User> user=  userRepository.findUserByEmailId(email);
        ResponseDto responseDto= new ResponseDto();
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmailId(email);
            newUser.setPassword(password);
            userRepository.save(newUser);
            responseDto.setUserDto(UserDto.from(newUser));
            responseDto.setError(null);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }else{
            responseDto.setError(new ErrorResponseDto("User already exists with given Email"));
            responseDto.setUserDto(null);
            return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
        }
    }
}
