package com.creadev.service;

import com.creadev.dto.request.category.CreateCategoryRequest;
import com.creadev.dto.request.category.UpdateCategoryRequest;
import com.creadev.dto.response.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    /**
     * Map the provided CreateCategoryRequest to a Category entity, persist it, and return the created response.
     *
     * @param request the request containing category details
     * @return the created CategoryResponse
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Get the total count of categories in the system.
     *
     * @return the total number of categories
     */
    Long getCategoryCount();

    /**
     * Fetch all Category entities and map them to responses with pagination.
     *
     * @param pageable the pagination information
     * @return a page of CategoryResponse
     */
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Fetch a Category by its ID or throw if not found, then map to response.
     *
     * @param categoryId the ID of the category to retrieve
     * @return the CategoryResponse corresponding to the given ID
     */
    CategoryResponse getCategoryById(Integer categoryId);

    /**
     * Load the Category entity by ID, apply updates from the request, persist changes, and return the updated response.
     *
     * @param categoryId the ID of the category to update
     * @param request    the request containing updated category details
     * @return the updated CategoryResponse
     */
    CategoryResponse updateCategory(Integer categoryId, UpdateCategoryRequest request);

    /**
     * Verify the category exists and has no associated documentations before deleting it.
     *
     * @param categoryId the ID of the category to delete
     */
    void deleteCategory(Integer categoryId);
} 