package co.kirikiri.common.dto;

import jakarta.validation.constraints.NotNull;

public record CustomScrollRequest(
        Long lastId,
        @NotNull(message = "사이즈를 입력해 주세요.")
        Integer size
) {

}
