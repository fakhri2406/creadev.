package com.creadev.controller;

import com.creadev.dto.request.product.CreateProductRequest;
import com.creadev.dto.request.product.UpdateProductRequest;
import com.creadev.dto.response.product.ProductResponse;
import com.creadev.service.ProductService;
import com.creadev.util.GlobalExceptionHandler;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
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

@WebMvcTest(controllers = ProductController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        com.creadev.config.jwt.SecurityConfig.class,
        com.creadev.config.jwt.JwtAuthenticationFilter.class,
        com.creadev.config.jwt.JwtSettings.class
    })
})
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, ProductControllerTest.MockConfig.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }

    private ProductResponse sampleProduct() {
        return new ProductResponse(
            10,
            5,
            "Sample Title",
            "Sample Description",
            "https://example.com",
            "https://cdn.example.com/image.jpg",
            Instant.parse("2024-01-01T00:00:00Z"),
            Instant.parse("2024-01-02T00:00:00Z")
        );
    }

    @Test
    @DisplayName("GET /api/v1/products/count returns 200 with count")
    void getProductCount_returnsOk() throws Exception {
        // Arrange
        when(productService.getProductCount()).thenReturn(7L);

        // Act
        mockMvc.perform(get("/api/v1/products/count"))
            // Assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/products returns 200 with paged products")
    void getAllProducts_returnsOkWithPage() throws Exception {
        // Arrange
        Page<ProductResponse> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(0, 10), 1);
        when(productService.getAllProducts(any())).thenReturn(page);

        // Act
        mockMvc.perform(get("/api/v1/products").param("page", "0").param("size", "10"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(10)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.size", is(10)))
            .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/products returns 204 when empty page")
    void getAllProducts_returnsNoContentWhenEmpty() throws Exception {
        // Arrange
        when(productService.getAllProducts(any())).thenReturn(Page.empty());

        // Act
        mockMvc.perform(get("/api/v1/products"))
            // Assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} returns 200 with product")
    void getProductById_returnsOk() throws Exception {
        // Arrange
        when(productService.getProductById(10)).thenReturn(sampleProduct());

        // Act
        mockMvc.perform(get("/api/v1/products/{id}", 10))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.categoryId", is(5)));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} returns 404 when not found")
    void getProductById_notFound() throws Exception {
        // Arrange
        when(productService.getProductById(999)).thenThrow(new NoSuchElementException("Product not found"));

        // Act
        mockMvc.perform(get("/api/v1/products/{id}", 999))
            // Assert
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    @DisplayName("GET /api/v1/products/category/{categoryId} returns 200 with list")
    void getProductsByCategory_returnsOk() throws Exception {
        // Arrange
        when(productService.getProductsByCategory(5)).thenReturn(List.of(sampleProduct()));

        // Act
        mockMvc.perform(get("/api/v1/products/category/{categoryId}", 5))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoryId", is(5)));
    }

    @Test
    @DisplayName("GET /api/v1/products/search returns 200 with results")
    void searchProducts_returnsOk() throws Exception {
        // Arrange
        Page<ProductResponse> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(0, 10), 1);
        when(productService.searchProducts(eq("term"), eq(null), any())).thenReturn(page);

        // Act
        mockMvc.perform(get("/api/v1/products/search").param("term", "term").param("page", "0").param("size", "10"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/v1/products returns 201 when valid multipart request")
    void createProduct_valid_returnsCreated() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes(StandardCharsets.UTF_8));
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(sampleProduct());

        // Act
        mockMvc.perform(
                multipart("/api/v1/products")
                    .file(image)
                    .param("categoryId", "5")
                    .param("title", "Sample Title")
                    .param("description", "Sample Description")
                    .param("link", "https://example.com")
            )
            // Assert
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /api/v1/products returns 400 when validation fails")
    void createProduct_invalid_returnsBadRequest() throws Exception {
        // Arrange
        MockMultipartFile emptyImage = new MockMultipartFile("image", new byte[0]);

        // Act
        mockMvc.perform(
                multipart("/api/v1/products")
                    .file(emptyImage)
                    .param("categoryId", "")
                    .param("title", "")
                    .param("description", "")
                    .param("link", "")
            )
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/products returns 409 on conflict")
    void createProduct_conflictOnDuplicate() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes(StandardCharsets.UTF_8));
        when(productService.createProduct(any(CreateProductRequest.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        // Act
        mockMvc.perform(
                multipart("/api/v1/products")
                    .file(image)
                    .param("categoryId", "5")
                    .param("title", "Sample Title")
                    .param("description", "Sample Description")
                    .param("link", "https://example.com")
            )
            // Assert
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} returns 200 when valid multipart")
    void updateProduct_valid_returnsOk() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes(StandardCharsets.UTF_8));
        when(productService.updateProduct(eq(10), any(UpdateProductRequest.class))).thenReturn(sampleProduct());

        // Act
        mockMvc.perform(
                multipart("/api/v1/products/{id}", 10)
                    .file(image)
                    .param("categoryId", "5")
                    .param("title", "Sample Title")
                    .param("description", "Sample Description")
                    .param("link", "https://example.com")
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    })
            )
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} returns 204 when deleted")
    void deleteProduct_returnsNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(10);

        // Act
        mockMvc.perform(delete("/api/v1/products/{id}", 10))
            // Assert
            .andExpect(status().isNoContent());
    }
}
