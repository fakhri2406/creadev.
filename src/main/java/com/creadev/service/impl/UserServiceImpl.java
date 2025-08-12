package com.creadev.service.impl;

import com.creadev.config.admin.AdminProperties;
import com.creadev.config.phone.PhoneProperties;
import com.creadev.domain.Role;
import com.creadev.domain.User;
import com.creadev.dto.request.user.CreateUserRequest;
import com.creadev.dto.request.user.UpdateUserRequest;
import com.creadev.dto.response.user.UserResponse;
import com.creadev.repository.RoleRepository;
import com.creadev.repository.UserRepository;
import com.creadev.service.UserService;
import com.creadev.util.AutoMapper;
import com.creadev.util.Hasher;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.creadev.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AutoMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Hasher hasher;
    private final PhoneProperties phoneProperties;
    private final AdminProperties adminProperties;

    /**
     * Create
     */
    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DataIntegrityViolationException(USERNAME_IN_USE);
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DataIntegrityViolationException(EMAIL_IN_USE);
        }
        if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new DataIntegrityViolationException(PHONE_IN_USE);
        }

        Role role = roleRepository.findById(request.roleId())
            .orElseThrow(() -> new IllegalStateException(ROLE_NOT_FOUND));

        String salt = UUID.randomUUID().toString();
        String passwordHash = hasher.hash(request.password() + salt);
        String fullPhone = phoneProperties.getPrefix() + request.phoneNumber();

        User user = User.builder()
            .username(request.username())
            .passwordHash(passwordHash)
            .passwordSalt(salt)
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .phoneNumber(fullPhone)
            .registeredAt(LocalDateTime.now())
            .roleId(role.getId())
            .role(role)
            .build();

        User saved = userRepository.save(user);
        return mapper.toUserResponse(saved);
    }

    /**
     * Read
     */
    @Override
    @Transactional(readOnly = true)
    public Long getUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(mapper::toUserResponse);
    }

    @Override
    @Transactional
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
        return mapper.toUserResponse(user);
    }

    /**
     * Update
     */
    @Override
    @Transactional
    public UserResponse updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        if (!user.getEmail().equals(request.email()) &&
            userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException(EMAIL_IN_USE);
        }
        if (!user.getPhoneNumber().equals(phoneProperties.getPrefix() + request.phoneNumber()) &&
            userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new IllegalArgumentException(PHONE_IN_USE);
        }

        Role role = roleRepository.findById(request.roleId())
            .orElseThrow(() -> new IllegalStateException(ROLE_NOT_FOUND));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(phoneProperties.getPrefix() + request.phoneNumber());
        user.setRoleId(role.getId());
        user.setRole(role);

        User updated = userRepository.save(user);
        return mapper.toUserResponse(updated);
    }

    /**
     * Delete
     */
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(adminProperties.getUsername())) {
            throw new IllegalArgumentException(CANNOT_DELETE_MASTER_ADMIN);
        }
        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(adminProperties.getEmail())) {
            throw new IllegalArgumentException(CANNOT_DELETE_MASTER_ADMIN);
        }

        userRepository.deleteById(userId);
    }
} 