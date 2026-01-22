package com.seezoon.infrastructure.dto;

public abstract class PageQuery {

    /**
     * 页码
     */
    private int page = 1;
    /**
     * 每页条数
     */
    private int pageSize = 10;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
