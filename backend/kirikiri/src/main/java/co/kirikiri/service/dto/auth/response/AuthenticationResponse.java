package co.kirikiri.service.dto.auth.response;

import java.util.Objects;

public record AuthenticationResponse(
        String refreshToken,
        String accessToken
) {

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthenticationResponse response = (AuthenticationResponse) o;
        return Objects.equals(refreshToken, response.refreshToken) && Objects.equals(accessToken, response.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refreshToken, accessToken);
    }
}
