package com.example.demo.dto;

/**
 * Registration response DTO.
 *
 * The password hash is never included in the response.
 * Only the essential non-sensitive user information is returned.
 */
public record RegisterResponse(
        Long id,
        String username,
        String role
) {}
