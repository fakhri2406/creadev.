package com.creadev.service.impl;

import com.creadev.domain.Product;
import com.creadev.dto.request.product.CreateProductRequest;
import com.creadev.dto.request.product.UpdateProductRequest;
import com.creadev.dto.response.product.ProductResponse;
import com.creadev.external.cloudinary.CloudinaryService;
import com.creadev.repository.ProductRepository;
import com.creadev.service.ProductService;
import com.creadev.util.AutoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.creadev.util.ErrorMessages.FAILED_RETRIEVE_PRODUCT;
import static com.creadev.util.ErrorMessages.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final AutoMapper mapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Create
     */
    @Override
    @Transactional
    public ProductResponse createProduct(@Valid CreateProductRequest request) {
        Product product = mapper.toProductFromCreate(request);
        product = productRepository.save(product);

        if (request.image() != null) {
            String url = cloudinaryService.uploadFile(request.image());
            product.setImageUrl(url);
        }

        Product saved = productRepository.findById(product.getId())
            .orElseThrow(() -> new IllegalStateException(FAILED_RETRIEVE_PRODUCT));
        return mapper.toProductResponse(saved);
    }

    /**
     * Read
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(mapper::toProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        return mapper.toProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        return productRepository.findAllByCategoryId(categoryId)
            .stream()
            .map(mapper::toProductResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String term, Integer categoryId, Pageable pageable) {
        Page<Product> productPage;

        if (categoryId != null) {
            productPage = productRepository.findByTitleContainingIgnoreCaseAndCategoryId(term, categoryId, pageable);
        } else {
            productPage = productRepository.findByTitleContainingIgnoreCase(term, pageable);
        }

        return productPage.map(mapper::toProductResponse);
    }

    /**
     * Update
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(Integer productId, @Valid UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        mapper.toProductFromUpdate(request, product);

        if (request.image() != null) {
            String oldUrl = product.getImageUrl();
            cloudinaryService.deleteFile(oldUrl);

            String url = cloudinaryService.uploadFile(request.image());
            product.setImageUrl(url);
        }

        Product updated = productRepository.save(product);
        return mapper.toProductResponse(updated);
    }

    /**
     * Delete
     */
    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        cloudinaryService.deleteFile(product.getImageUrl());

        productRepository.deleteById(productId);
    }
} 