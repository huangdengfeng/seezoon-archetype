package com.seezoon.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class PageQuery {

    /**
     * 页码
     */
    @Schema(title = "页码", description = "默认为1")
    private int page = 1;
    /**
     * 每页条数
     */
    @Schema(title = "每页条数", description = "默认10")
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
