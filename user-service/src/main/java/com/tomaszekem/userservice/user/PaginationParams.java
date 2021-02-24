package com.tomaszekem.userservice.user;

import javax.validation.constraints.Max;

public class PaginationParams {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private Integer page;
    @Max(1000)
    private Integer perPage;

    public Integer getPage() {
        return page != null ? page : 0;
    }

    public Integer getPerPage() {
        return perPage != null ? perPage : DEFAULT_PAGE_SIZE;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }
}
