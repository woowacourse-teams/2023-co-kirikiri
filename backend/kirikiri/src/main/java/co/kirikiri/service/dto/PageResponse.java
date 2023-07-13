package co.kirikiri.service.dto;

import java.util.List;

public record PageResponse<T>(
    int currentPage,
    int totalPage,
    List<T> data
) {

}
