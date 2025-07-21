package com.creadev.util;

import com.creadev.domain.Category;
import com.creadev.domain.Product;
import com.creadev.domain.User;
import com.creadev.dto.request.category.CreateCategoryRequest;
import com.creadev.dto.request.category.UpdateCategoryRequest;
import com.creadev.dto.request.product.CreateProductRequest;
import com.creadev.dto.request.product.UpdateProductRequest;
import com.creadev.dto.response.category.CategoryResponse;
import com.creadev.dto.response.product.ProductResponse;
import com.creadev.dto.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AutoMapper {
    /**
     * Category mapping
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "products", ignore = true)
    Category toCategoryFromCreate(CreateCategoryRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    void toCategoryFromUpdate(UpdateCategoryRequest dto, @MappingTarget Category category);

    @Mapping(target = "createdAt", expression = "java(mapToInstant(category.getCreatedAt()))")
    CategoryResponse toCategoryResponse(Category category);

    /**
     * Product mapping
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Product toProductFromCreate(CreateProductRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void toProductFromUpdate(UpdateProductRequest dto, @MappingTarget Product product);

    @Mapping(target = "createdAt", expression = "java(mapToInstant(documentation.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(mapToInstant(documentation.getUpdatedAt()))")
    ProductResponse toProductResponse(Product documentation);

    /**
     * User mapping
     */
    @Mapping(target = "registeredAt", expression = "java(mapToInstant(user.getRegisteredAt()))")
    @Mapping(target = "lastLoginAt", expression = "java(mapToInstant(user.getLastLoginAt()))")
    @Mapping(target = "role", source = "role.title")
    UserResponse toUserResponse(User user);

    /**
     * Helper
     */
    default Instant mapToInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toInstant(ZoneOffset.UTC);
    }
} 