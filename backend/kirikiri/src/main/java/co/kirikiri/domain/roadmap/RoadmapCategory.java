package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapCategory extends BaseEntity {

    @Column(length = 15, nullable = false)
    private String name;

    public RoadmapCategory(final String name) {
        this(null, name);
    }

    public RoadmapCategory(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
