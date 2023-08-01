package co.kirikiri.service.dto;

import java.util.List;

// TODO 페이징 -> 무한스크롤로 바뀌면서 제거 예정인 DTO
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
