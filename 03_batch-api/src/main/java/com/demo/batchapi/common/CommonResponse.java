package com.demo.batchapi.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommonResponse<T> {
    private T data;
    private String message;
    private int status;

    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setData(data);
        response.setStatus(200);
        return response;
    }
}
