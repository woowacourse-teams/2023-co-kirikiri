package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapTag extends BaseEntity {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 10;

    @Column(length = 15)
    private String name;

    public RoadmapTag(final String name) {
        validate(name);
        this.name = name;
    }

    public RoadmapTag(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    private void validate(final String name) {
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("태그 이름은 최소 %d자부터 최대 %d자까지 가능합니다.", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
