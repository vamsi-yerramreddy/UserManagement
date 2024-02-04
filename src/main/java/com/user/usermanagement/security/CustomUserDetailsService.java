package com.user.usermanagement.security;

import com.user.usermanagement.model.User;
import com.user.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findUserByEmailId(emailId);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }else {
            return new CustomUserDetails(user.get());
        }
    }
}
