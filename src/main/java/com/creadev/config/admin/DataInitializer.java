package com.creadev.config.admin;

import com.creadev.config.phone.PhoneProperties;
import com.creadev.domain.Role;
import com.creadev.domain.User;
import com.creadev.repository.RoleRepository;
import com.creadev.repository.UserRepository;
import com.creadev.util.Hasher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final Hasher hasher;
    private final AdminProperties adminProperties;
    private final PhoneProperties phoneProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> roles = List.of("ADMIN", "EDITOR");
        for (String roleName : roles) {
            roleRepository.findByTitle(roleName).orElseGet(() -> {
                Role role = Role.builder().title(roleName).build();
                return roleRepository.save(role);
            });
        }

        String username = adminProperties.getUsername();
        if (userRepository.findByUsername(username).isEmpty()) {
            Role adminRole = roleRepository.findByTitle("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

            String salt = UUID.randomUUID().toString();

            User admin = User.builder()
                .username(adminProperties.getUsername())
                .passwordHash(hasher.hash(adminProperties.getPassword() + salt))
                .passwordSalt(salt)
                .firstName(adminProperties.getFirstName())
                .lastName(adminProperties.getLastName())
                .email(adminProperties.getEmail())
                .phoneNumber(phoneProperties.getPrefix() + adminProperties.getPhoneNumber())
                .registeredAt(LocalDateTime.now())
                .roleId(adminRole.getId())
                .build();

            userRepository.save(admin);
        }
    }
}