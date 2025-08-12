package com.creadev.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    /**
     * Auth error messages
     */
    public static final String USERNAME_IN_USE = "Username is already in use";
    public static final String EMAIL_IN_USE = "Email is already in use";
    public static final String PHONE_IN_USE = "Phone number is already in use";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String INVALID_ACCESS_TOKEN = "Invalid access token";
    public static final String ACCESS_TOKEN_REVOKED = "Access token has been revoked";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token expired";
    public static final String REFRESH_TOKEN_MISMATCH = "Refresh token does not belong to current user";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String CANNOT_DELETE_MASTER_ADMIN = "Cannot delete the master admin user";


    /**
     * Category error messages
     */
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CANNOT_DELETE_CATEGORY_WITH_DOCUMENTATIONS = "Cannot delete category with products";

    /**
     * Product error messages
     */
    public static final String FAILED_RETRIEVE_PRODUCT = "Failed to retrieve saved product";
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    /**
     * Cloudinary error messages
     */
    public static final String UPLOAD_FAILED = "Failed to upload file to Cloudinary";
    public static final String DELETE_FAILED = "Failed to delete file from Cloudinary";
    public static final String FILE_EMPTY_OR_NULL = "File is empty or null";
    public static final String FILE_SIZE_EXCEEDS_LIMIT = "File size exceeds the maximum allowed size of %d bytes";
    public static final String FILE_UNSUPPORTED_TYPE = "Unsupported file type. Allowed types: JPEG, PNG, PDF";

    /**
     * OpenAI error messages
     */
    public static final String FAILED_TO_SEND_CHAT_REQUEST = "Failed to send chat request";
    public static final String FAILED_TO_PREPARE_AI_REQUEST = "Failed to prepare AI request";

    /**
     * Email error messages
     */
    public static final String FAILED_TO_SEND_EMAIL = "Failed to send email";
} 