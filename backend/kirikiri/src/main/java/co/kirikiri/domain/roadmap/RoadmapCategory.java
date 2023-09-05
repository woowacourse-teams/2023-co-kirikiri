package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadmapCategory extends BaseEntity {

    private static final int MAX_NAME_LENGTH = 10;

    @Column(length = 15, nullable = false)
    private String name;

    public RoadmapCategory(final String name) {
        this(null, name);
    }

    public RoadmapCategory(final Long id, final String name) {
        super.id = id;
        validateNameLength(name);
        this.name = name;
    }

    private void validateNameLength(final String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new BadRequestException("카테고리 이름은 10자 이하입니다.");
        }
    }

    public String getName() {
        return name;
    }
}
