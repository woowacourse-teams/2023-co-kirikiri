package co.kirikiri.persistence.dto;

public record RoadmapSearchCreatorNickname(
        String value
){

    public RoadmapSearchCreatorNickname(final String value) {
        this.value = trim(value);
    }

    private String trim(final String nickname) {
        return nickname.trim();
    }
}
