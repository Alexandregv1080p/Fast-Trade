package com.fasttrade.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;

    public static <T> PageResponse<T> of(List<T> data, int page, int pageSize, long total) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return new PageResponse<>(data, page, pageSize, total, totalPages);
    }
}
