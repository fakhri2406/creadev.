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

    /**
     * Cloudinary error messages
     */
    public static final String UPLOAD_FAILED = "Failed to upload file to Cloudinary";
    public static final String DELETE_FAILED = "Failed to delete file from Cloudinary";
    public static final String FILE_EMPTY_OR_NULL = "File is empty or null";
    public static final String FILE_SIZE_EXCEEDS_LIMIT = "File size exceeds the maximum allowed size of %d bytes";
    public static final String FILE_UNSUPPORTED_TYPE = "Unsupported file type. Allowed types: JPEG, PNG, PDF";
} 