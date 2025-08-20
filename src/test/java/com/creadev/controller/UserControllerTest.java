package com.creadev.controller;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.creadev.dto.request.user.CreateUserRequest;
import com.creadev.dto.request.user.UpdateUserRequest;
import com.creadev.dto.response.user.UserResponse;
import com.creadev.service.UserService;
import com.creadev.util.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        com.creadev.config.jwt.SecurityConfig.class,
        com.creadev.config.jwt.JwtAuthenticationFilter.class,
        com.creadev.config.jwt.JwtSettings.class
    })
})
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, UserControllerTest.MockConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    private UserResponse sampleUser() {
        return new UserResponse(
            1,
            "johndoe",
            "John",
            "Doe",
            "john.doe@example.com",
            "501234567",
            Instant.parse("2024-01-01T00:00:00Z"),
            Instant.parse("2024-02-01T00:00:00Z"),
            2,
            "EDITOR"
        );
    }

    @Test
    @DisplayName("GET /api/v1/users/count returns 200 with count")
    void getUserCount_returnsOk() throws Exception {
        // Arrange
        when(userService.getUserCount()).thenReturn(5L);

        // Act
        mockMvc.perform(get("/api/v1/users/count"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("GET /api/v1/users returns 200 with paged users")
    void getAllUsers_returnsOkWithPage() throws Exception {
        // Arrange
        Page<UserResponse> page = new PageImpl<>(List.of(sampleUser()), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any())).thenReturn(page);

        // Act
        mockMvc.perform(get("/api/v1/users").param("page", "0").param("size", "10"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].username", is("johndoe")))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.size", is(10)))
            .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/users returns 204 when empty page")
    void getAllUsers_returnsNoContentWhenEmpty() throws Exception {
        // Arrange
        when(userService.getAllUsers(any())).thenReturn(Page.empty());

        // Act
        mockMvc.perform(get("/api/v1/users"))
            // Assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns 200 with user")
    void getUserById_returnsOk() throws Exception {
        // Arrange
        when(userService.getUserById(1)).thenReturn(sampleUser());

        // Act
        mockMvc.perform(get("/api/v1/users/{id}", 1))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns 404 when not found")
    void getUserById_notFound() throws Exception {
        // Arrange
        when(userService.getUserById(999)).thenThrow(new NoSuchElementException("User not found"));

        // Act
        mockMvc.perform(get("/api/v1/users/{id}", 999))
            // Assert
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    @DisplayName("POST /api/v1/users returns 201 when valid request")
    void createUser_valid_returnsCreated() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "johndoe",
            "P@ssw0rd!",
            "John",
            "Doe",
            "john.doe@example.com",
            "501234567",
            2
        );
        when(userService.createUser(any())).thenReturn(sampleUser());

        // Act
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    @DisplayName("POST /api/v1/users returns 400 when validation fails")
    void createUser_invalid_returnsBadRequest() throws Exception {
        // Arrange
        CreateUserRequest invalid = new CreateUserRequest(
            "",
            "short",
            "John1",
            "",
            "invalid",
            "123",
            null
        );

        // Act
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/users returns 409 when service reports data integrity violation")
    void createUser_conflictOnDuplicate() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "johndoe",
            "P@ssw0rd!",
            "John",
            "Doe",
            "john.doe@example.com",
            "501234567",
            2
        );
        when(userService.createUser(any())).thenThrow(new DataIntegrityViolationException("Duplicate email"));

        // Act
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.message", containsString("Duplicate email")));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} returns 200 with updated user")
    void updateUser_valid_returnsOk() throws Exception {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest(
            "John",
            "Doe",
            "john.doe@example.com",
            "501234567",
            2
        );
        when(userService.updateUser(eq(1), any())).thenReturn(sampleUser());

        // Act
        mockMvc.perform(put("/api/v1/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.role", is("EDITOR")));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} returns 204 when deleted")
    void deleteUser_returnsNoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1);

        // Act
        mockMvc.perform(delete("/api/v1/users/{id}", 1))
            // Assert
            .andExpect(status().isNoContent());
    }
}
