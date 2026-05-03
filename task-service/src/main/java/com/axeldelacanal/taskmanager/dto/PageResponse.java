package com.axeldelacanal.taskmanager.dto;

import java.util.List;

public class PageResponse<T> {

    public List<T> content;
    public int page;
    public int size;
    public long total;
    public int totalPages;

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.content = content;
        response.page = page;
        response.size = size;
        response.total = total;
        response.totalPages = (int) Math.ceil((double) total / size);
        return response;
    }
}
