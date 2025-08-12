package com.creadev.service.impl;

import com.creadev.domain.Category;
import com.creadev.dto.request.category.CreateCategoryRequest;
import com.creadev.dto.request.category.UpdateCategoryRequest;
import com.creadev.dto.response.category.CategoryResponse;
import com.creadev.repository.CategoryRepository;
import com.creadev.repository.ProductRepository;
import com.creadev.service.CategoryService;
import com.creadev.util.AutoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.creadev.util.ErrorMessages.CANNOT_DELETE_CATEGORY_WITH_DOCUMENTATIONS;
import static com.creadev.util.ErrorMessages.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AutoMapper mapper;

    /**
     * Create
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(@Valid CreateCategoryRequest request) {
        Category category = mapper.toCategoryFromCreate(request);
        category = categoryRepository.save(category);
        return mapper.toCategoryResponse(category);
    }

    /**
     * Read
     */
    @Override
    @Transactional(readOnly = true)
    public Long getCategoryCount() {
        return categoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
            .map(mapper::toCategoryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(CATEGORY_NOT_FOUND));
        return mapper.toCategoryResponse(category);
    }

    /**
     * Update
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, @Valid UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException(CATEGORY_NOT_FOUND));
        mapper.toCategoryFromUpdate(request, category);
        category = categoryRepository.save(category);
        return mapper.toCategoryResponse(category);
    }

    /**
     * Delete
     */
    @Override
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NoSuchElementException(CATEGORY_NOT_FOUND);
        }

        if (productRepository.existsByCategoryId(categoryId)) {
            throw new DataIntegrityViolationException(CANNOT_DELETE_CATEGORY_WITH_DOCUMENTATIONS);
        }

        categoryRepository.deleteById(categoryId);
    }
} 