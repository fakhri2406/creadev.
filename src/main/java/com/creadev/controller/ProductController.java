package com.creadev.controller;

import com.creadev.dto.request.product.CreateProductRequest;
import com.creadev.dto.request.product.UpdateProductRequest;
import com.creadev.dto.response.product.ProductResponse;
import com.creadev.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(
    name = "Product",
    description = "Endpoints for managing products"
)
@RequestMapping("/api/v1/docs")
@Validated
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Retrieve a paginated list of all products"
    )
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> result = productService.getAllProducts(pageable);

        return result.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a product entry by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
        summary = "Get products by category",
        description = "Retrieve products for the specified category"
    )
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @Operation(
        summary = "Search products (paginated)",
        description = "Search products by term and optional category with pagination"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
        @RequestParam String term,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(term, categoryId, pageable));
    }

    @Operation(
        summary = "Create a new product",
        description = "Create a new product",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
        @ModelAttribute @Valid CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Update an existing product",
        description = "Update a product by its ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Integer id,
        @ModelAttribute @Valid UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a product",
        description = "Delete product by its ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
} 
