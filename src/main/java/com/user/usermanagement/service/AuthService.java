package com.user.usermanagement.service;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.user.usermanagement.controller.AuthController;
import com.user.usermanagement.dto.ErrorResponseDto;
import com.user.usermanagement.dto.ResponseDto;
import com.user.usermanagement.dto.TokenResponseDto;
import com.user.usermanagement.dto.UserDto;
import com.user.usermanagement.model.Session;
import com.user.usermanagement.model.SessionStatus;
import com.user.usermanagement.model.User;
import com.user.usermanagement.repository.SessionRepository;
import com.user.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    @Autowired
    public AuthService( UserRepository userRepository, SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
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

            Session session = new Session();
            String Token = "Random token";
            session.setToken(Token);
            session.setStatus(SessionStatus.ACTIVE);
            session.setUser(user.get());
            sessionRepository.save(session);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Session-token", Token);

            return new ResponseEntity<>(responseDto,headers, HttpStatus.OK);

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

    public ResponseEntity<TokenResponseDto> validate(String token, Long userId){
         Optional<Session> session=  sessionRepository.findByToken(token);
         if(session.isPresent() &&
         session.get().getUser().getId().equals(userId) ){
             return new ResponseEntity<>(new TokenResponseDto("Token is valid"), HttpStatus.OK);
         }else{
             return new ResponseEntity<>(new TokenResponseDto("Token is invalid"), HttpStatus.UNAUTHORIZED);
         }
    }
}
