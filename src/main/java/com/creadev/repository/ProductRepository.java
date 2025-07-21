package com.creadev.repository;

import com.creadev.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByCategoryId(Integer categoryId);

    List<Product> findAllByCategoryId(Integer categoryId);

    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Product> findByTitleContainingIgnoreCaseAndCategoryId(String title, Integer categoryId, Pageable pageable);
} 