package com.user.usermanagement.service;

import com.user.usermanagement.dto.ErrorResponseDto;
import com.user.usermanagement.dto.ResponseDto;
import com.user.usermanagement.dto.TokenResponseDto;
import com.user.usermanagement.dto.UserDto;
import com.user.usermanagement.model.PasswordResetToken;
import com.user.usermanagement.model.Session;
import com.user.usermanagement.model.SessionStatus;
import com.user.usermanagement.model.User;
import com.user.usermanagement.repository.PasswordResetTokenRepository;
import com.user.usermanagement.repository.SessionRepository;
import com.user.usermanagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
public class AuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private JavaMailSender javaMailSender;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordResetToken passwordResetToken;
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private SecretKey key;
    private MacAlgorithm algo;

    @PostConstruct
    public void init(){
        algo = Jwts.SIG.HS256;
        key = algo.key().build();
    }

    @Autowired
    public AuthService( UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                        SessionRepository sessionRepository,
                        PasswordResetToken passwordResetToken,
                        JavaMailSender javaMailSender,
                        PasswordResetTokenRepository passwordResetTokenRepository){
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder= passwordEncoder;
        this.javaMailSender= javaMailSender;
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenRepository = passwordResetTokenRepository;

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
        if(bCryptPasswordEncoder.matches(password, user.get().getPassword()) ) {
            responseDto.setUserDto(UserDto.from(user.get()));
            responseDto.setError(null);

            Session session = new Session();

            String Token = generateJwt(user.get());

            session.setStatus(SessionStatus.ACTIVE);
            session.setUser(user.get());
            session.setToken(Token);
            session.setExpiryTime(new Date(System.currentTimeMillis() + 40 * 60 * 1000 ));
            sessionRepository.save(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer "+ Token);

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
            newUser.setPassword(bCryptPasswordEncoder.encode(password));
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
            System.out.println("session: " + session.toString());

         if(session.isPresent() &&
         session.get().getUser().getId().equals(userId) ){
         //if(session.isPresent() ){
             /*Decode the JWT token */

             Claims claims = Jwts.parser()
                     .setSigningKey(key).build().parseClaimsJws(token).getBody();
                //System.out.println("claims: " + claims);
             return new ResponseEntity<>(new TokenResponseDto("Token is valid"), HttpStatus.OK);
         }else{

             return new ResponseEntity<>(new TokenResponseDto("Token is invalid"), HttpStatus.UNAUTHORIZED);
         }
    }

    public String generateJwt(User user){
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", user.getEmailId());
        payload.put("userId", user.getId());
        payload.put("createdAt", System.currentTimeMillis());
        System.out.println("key: " + key);
        return  Jwts.builder().
                setClaims(payload).signWith(key,algo).compact();
    }

    public ResponseEntity<?>  resetPassword(String emailId){
        Optional<User> user = userRepository.findUserByEmailId(emailId);
        if(user.isEmpty()){
            return new ResponseEntity<>(new ErrorResponseDto("User not found"), HttpStatus.NOT_FOUND);

        }else{
            String Token = Jwts.builder()
                            .setSubject(emailId)
                            .setExpiration(new Date(System.currentTimeMillis() + 20 * 60 * 1000 ))
                            .signWith(key,algo)
                            .compact();
          //  PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(Token);
            passwordResetToken.setUser(user.get());
            passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis() + 20 * 60 * 1000 ));
            passwordResetTokenRepository.save(passwordResetToken);
            sendResetPasswordEmail(emailId,Token);
            return new ResponseEntity<>(new TokenResponseDto("Password reset link sent to your email"), HttpStatus.OK);
        }
    }
    public void sendResetPasswordEmail(String emailId,String token  ){

        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(emailId);
        message.setSubject("Password Reset Link");
        String passwordResetLink = "http://localhost:8080/auth/reset?token="+token;
        message.setText("Click on the link to reset your password: " + passwordResetLink);
        javaMailSender.send(message);
    }

public ResponseEntity<?> resetPasswordConfirm(String token, String newPassword){
    Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
    if(passwordResetToken.isEmpty()){
        return new ResponseEntity<>(new ErrorResponseDto("Token is invalid"), HttpStatus.UNAUTHORIZED);
    }
    else if(passwordResetToken.get().getExpiryDate().before(new Date())){
        return new ResponseEntity<>(new ErrorResponseDto("Token has expired"), HttpStatus.UNAUTHORIZED);
    }
    else{
        User user = passwordResetToken.get().getUser();
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

}

