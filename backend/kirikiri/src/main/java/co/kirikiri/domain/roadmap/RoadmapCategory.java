package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadmapCategory extends BaseEntity {

    @Column(length = 15, nullable = false)
    private String name;

    public RoadmapCategory(final String name) {
        this(null, name);
    }

    public RoadmapCategory(final Long id, final String name) {
        super.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
