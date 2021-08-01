package com.den.marvel.marvel.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class ListResponse<T> {

    private final Page<T> page;

    public ListResponse(Page<T> page) {
        this.page = page;
    }

    public List<T> getData()
    {
        return page.getContent();
    }

    public long getTotalItems() {
        return page.getTotalElements();
    }

    public int getCurrentPage() {
        return page.getNumber();
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }
}
