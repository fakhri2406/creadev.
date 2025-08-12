package com.creadev.service;

import com.creadev.dto.request.product.CreateProductRequest;
import com.creadev.dto.request.product.UpdateProductRequest;
import com.creadev.dto.response.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    /**
     * Map the provided CreateProductRequest to a Category entity, persist it, upload image to Cloudinary and return the created response.
     *
     * @param request the request containing product details
     * @return the created ProductResponse
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Get the total count of products in the system.
     *
     * @return the total number of products
     */
    Long getProductCount();

    /**
     * Fetch all Product entities and map them to responses with pagination.
     *
     * @param pageable the pagination information
     * @return a page of ProductResponse
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Fetch a Product by its ID or throw if not found, then map to response.
     *
     * @param productId the ID of the product to retrieve
     * @return the ProductResponse corresponding to the given ID
     */
    ProductResponse getProductById(Integer productId);

    /**
     * Fetch all Product entities for the given category and map them to responses.
     *
     * @param categoryId the ID of the category to filter products
     * @return a list of ProductResponse for the specified category
     */
    List<ProductResponse> getProductsByCategory(Integer categoryId);

    /**
     * Search product entries by title (case-insensitive) and optional category filter with pagination.
     *
     * @param term       the search term to filter products
     * @param categoryId the ID of the category to filter product (nullable)
     * @param pageable   the pagination information
     * @return a page of ProductResponse matching the search criteria
     */
    Page<ProductResponse> searchProducts(String term, Integer categoryId, Pageable pageable);

    /**
     * Load the Product entity by ID, apply field updates (including Cloudinary file operations), and return the updated response.
     *
     * @param productId the ID of the product to update
     * @param request   the request containing updated product details
     * @return the updated ProductResponse
     */
    ProductResponse updateProduct(Integer productId, UpdateProductRequest request);

    /**
     * Remove the Product entity and delete associated files from Cloudinary.
     *
     * @param productId the ID of the product to delete
     */
    void deleteProduct(Integer productId);
} 