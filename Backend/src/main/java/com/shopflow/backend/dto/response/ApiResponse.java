package com.shopflow.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int status, String message, T data, LocalDateTime timestamp) {

    //200 e 201
    public static <T> ApiResponse<T> success(String message, T data, HttpStatus httpStatus) {
        return new ApiResponse<>(httpStatus.value(), message, data, LocalDateTime.now());
    }

    //204
    public static ApiResponse<Void> success(String message, HttpStatus httpStatus) {
        return new ApiResponse<>(httpStatus.value(), message, null, LocalDateTime.now());
    }

    //400, 404 e 500
    public static ApiResponse<Void> error(String message, HttpStatus httpStatus) {
        return new ApiResponse<>(httpStatus.value(), message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T details, HttpStatus httpStatus) {
        return new ApiResponse<>(httpStatus.value(), message, details, LocalDateTime.now());
    }
}

