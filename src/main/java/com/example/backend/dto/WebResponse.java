package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebResponse<T> {
    private String message;
    private Integer status;
    private T data;
    private Object errors;

    public WebResponse(String message, Integer status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
