package com.creadev.config.jwt;

import com.creadev.domain.User;
import com.creadev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null && user.getRole().getTitle() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getTitle()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), authorities);
    }
} 