package com.example.licencespring.service;

import com.example.licencespring.model.User;
import com.example.licencespring.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }

    public boolean verifyOtp(String username, int otp) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.isTwoFactorEnabled()) {
            return false;
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(user.getTwoFactorSecret(), otp);
    }
}
