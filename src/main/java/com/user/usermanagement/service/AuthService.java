package com.user.usermanagement.service;

import com.user.usermanagement.dto.ErrorResponseDto;
import com.user.usermanagement.dto.ResponseDto;
import com.user.usermanagement.dto.TokenResponseDto;
import com.user.usermanagement.dto.UserDto;
import com.user.usermanagement.model.Session;
import com.user.usermanagement.model.SessionStatus;
import com.user.usermanagement.model.User;
import com.user.usermanagement.repository.SessionRepository;
import com.user.usermanagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
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

            String Token = generateJwt(user.get());

            session.setStatus(SessionStatus.ACTIVE);
            session.setUser(user.get());
            session.setToken(Token);
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
            System.out.println("session: " + session);

         //if(session.isPresent() &&
         //session.get().getUser().getId().equals(userId) )
         if(session.isPresent() ){
             /*Decode the JWT token */
              MacAlgorithm algorithm = Jwts.SIG.HS256;
              SecretKey key = algorithm.key().build();

             Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
                System.out.println("claims: " + claims);
             return new ResponseEntity<>(new TokenResponseDto("Token is valid"), HttpStatus.OK);
         }else{

             return new ResponseEntity<>(new TokenResponseDto("Token is invalid"+ token), HttpStatus.UNAUTHORIZED);
         }
    }

    public String generateJwt(User user){
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", user.getEmailId());
        payload.put("userId", user.getId());
        payload.put("createdAt", System.currentTimeMillis());

        MacAlgorithm algo = Jwts.SIG.HS256;
        SecretKey key = algo.key().build();
        
        System.out.println("key: " + key);
        return  Jwts.builder().content(payload.toString()).signWith(key,algo).compact();
    }
}
