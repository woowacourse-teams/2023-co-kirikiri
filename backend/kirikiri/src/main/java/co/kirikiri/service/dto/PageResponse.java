package co.kirikiri.service.dto;

import java.util.List;

public record PageResponse<T>(
    int currentPage,
    int totalPage,
    List<T> data
) {

    public PageResponse(final int currentPage, final int totalPage, final List<T> data) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.data = data;
    }
}
