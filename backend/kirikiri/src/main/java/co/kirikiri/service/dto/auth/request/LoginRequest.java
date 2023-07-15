package co.kirikiri.service.dto.auth.request;

public record LoginRequest(
    String identifier,
    String password
) {

}
