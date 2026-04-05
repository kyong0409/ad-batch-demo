package com.demo.batchapi.common;

import lombok.Getter;
import java.util.List;

@Getter
public class ResponsePage<T> {
    private List<T> result;
    private PageInfo page;

    public static <T> ResponsePage<T> of(List<T> result, int page, int size, long totalElements) {
        ResponsePage<T> rp = new ResponsePage<>();
        rp.result = result;
        rp.page = new PageInfo(page + 1, size, totalElements); // UI expects 1-based page
        return rp;
    }
}
