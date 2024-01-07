package com.example.demo.api;

import com.example.demo.domain.MemberEntity;
import org.springframework.http.HttpStatus;

public record ApiResponseDto<T> (String message, T data) {

    public ApiResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
    public static <T> ApiResponseDto<T> success(T body) {
        return new ApiResponseDto<T>("SUCCESS", body);
    }

    public static <T> ApiResponseDto<T> success(HttpStatus httpStatus) {
        return new ApiResponseDto<>(httpStatus.getReasonPhrase(), null);
    }

    public static <T> ApiResponseDto<MemberEntity> success(HttpStatus httpStatus, T body) {
        return (ApiResponseDto<MemberEntity>) new ApiResponseDto<>(httpStatus.getReasonPhrase(), body);
    }
}
