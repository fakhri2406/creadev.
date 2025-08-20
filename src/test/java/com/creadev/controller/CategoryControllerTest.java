package com.creadev.controller;

import com.creadev.dto.request.category.CreateCategoryRequest;
import com.creadev.dto.request.category.UpdateCategoryRequest;
import com.creadev.dto.response.category.CategoryResponse;
import com.creadev.service.CategoryService;
import com.creadev.util.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        com.creadev.config.jwt.SecurityConfig.class,
        com.creadev.config.jwt.JwtAuthenticationFilter.class,
        com.creadev.config.jwt.JwtSettings.class
    })
})
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, CategoryControllerTest.MockConfig.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }
    }

    private CategoryResponse sampleCategory() {
        return new CategoryResponse(
            3,
            "Web Apps",
            "All web application projects",
            Instant.parse("2024-01-01T00:00:00Z")
        );
    }

    @Test
    @DisplayName("GET /api/v1/categories/count returns 200 with count")
    void getCategoryCount_returnsOk() throws Exception {
        // Arrange
        when(categoryService.getCategoryCount()).thenReturn(4L);

        // Act
        mockMvc.perform(get("/api/v1/categories/count"))
            // Assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/categories returns 200 with paged categories")
    void getAllCategories_returnsOkWithPage() throws Exception {
        // Arrange
        Page<CategoryResponse> page = new PageImpl<>(List.of(sampleCategory()), PageRequest.of(0, 10), 1);
        when(categoryService.getAllCategories(any())).thenReturn(page);

        // Act
        mockMvc.perform(get("/api/v1/categories").param("page", "0").param("size", "10"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(3)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.size", is(10)))
            .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/categories returns 204 when empty page")
    void getAllCategories_returnsNoContentWhenEmpty() throws Exception {
        // Arrange
        when(categoryService.getAllCategories(any())).thenReturn(Page.empty());

        // Act
        mockMvc.perform(get("/api/v1/categories"))
            // Assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} returns 200 with category")
    void getCategoryById_returnsOk() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(3)).thenReturn(sampleCategory());

        // Act
        mockMvc.perform(get("/api/v1/categories/{id}", 3))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.title", is("Web Apps")));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} returns 404 when not found")
    void getCategoryById_notFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999)).thenThrow(new NoSuchElementException("Category not found"));

        // Act
        mockMvc.perform(get("/api/v1/categories/{id}", 999))
            // Assert
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.message", is("Category not found")));
    }

    @Test
    @DisplayName("POST /api/v1/categories returns 201 when valid request")
    void createCategory_valid_returnsCreated() throws Exception {
        // Arrange
        CreateCategoryRequest req = new CreateCategoryRequest("Web Apps", "All web application projects");
        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(sampleCategory());

        // Act
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)));
    }

    @Test
    @DisplayName("POST /api/v1/categories returns 400 when validation fails")
    void createCategory_invalid_returnsBadRequest() throws Exception {
        // Arrange
        CreateCategoryRequest invalid = new CreateCategoryRequest("", "x".repeat(300));

        // Act
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/categories returns 409 on conflict")
    void createCategory_conflictOnDuplicate() throws Exception {
        // Arrange
        CreateCategoryRequest req = new CreateCategoryRequest("Web Apps", "All web application projects");
        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        // Act
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/v1/categories/{id} returns 200 with updated category")
    void updateCategory_valid_returnsOk() throws Exception {
        // Arrange
        UpdateCategoryRequest req = new UpdateCategoryRequest("Web Apps", "Updated");
        when(categoryService.updateCategory(eq(3), any(UpdateCategoryRequest.class))).thenReturn(sampleCategory());

        // Act
        mockMvc.perform(put("/api/v1/categories/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)));
    }

    @Test
    @DisplayName("PUT /api/v1/categories/{id} returns 404 when not found")
    void updateCategory_notFound_returns404() throws Exception {
        // Arrange
        UpdateCategoryRequest req = new UpdateCategoryRequest("Web Apps", "Updated");
        when(categoryService.updateCategory(eq(999), any(UpdateCategoryRequest.class))).thenThrow(new NoSuchElementException("Category not found"));

        // Act
        mockMvc.perform(put("/api/v1/categories/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Category not found")));
    }

    @Test
    @DisplayName("DELETE /api/v1/categories/{id} returns 204 when deleted")
    void deleteCategory_returnsNoContent() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(3);

        // Act
        mockMvc.perform(delete("/api/v1/categories/{id}", 3))
            // Assert
            .andExpect(status().isNoContent());
    }
}
