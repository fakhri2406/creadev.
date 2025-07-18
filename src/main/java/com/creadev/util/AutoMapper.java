package com.creadev.util;

import com.creadev.domain.User;
import com.creadev.dto.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AutoMapper {
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