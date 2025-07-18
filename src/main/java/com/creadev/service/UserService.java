package com.creadev.service;

import com.creadev.dto.request.user.CreateUserRequest;
import com.creadev.dto.request.user.UpdateUserRequest;
import com.creadev.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    /**
     * Validate uniqueness of username, email, and phone; hash the password; persist the new User; and return the mapped response.
     *
     * @param request the CreateUserRequest containing user details
     * @return a UserResponse representing the created user
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Fetch all User entities and map to UserResponse DTOs with pagination.
     *
     * @param pageable the pagination information
     * @return a page of UserResponse objects representing users
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Fetch a User by ID or throw if not found, then map to UserResponse DTO.
     *
     * @param userId the ID of the user to retrieve
     * @return a UserResponse containing the user's details
     */
    UserResponse getUserById(Integer userId);

    /**
     * Validate updated fields (email and phone uniqueness), apply changes to the User entity, save, and return the updated response.
     *
     * @param userId  the ID of the user to update
     * @param request the UpdateUserRequest containing updated user information
     * @return a UserResponse representing the updated user
     */
    UserResponse updateUser(Integer userId, UpdateUserRequest request);

    /**
     * Verify the user exists and delete it from the repository.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(Integer userId);
} 