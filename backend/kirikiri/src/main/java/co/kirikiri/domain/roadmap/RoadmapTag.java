package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapTag extends BaseEntity {

    @Embedded
    private RoadmapTagName name;

    public RoadmapTag(final RoadmapTagName name) {
        this.name = name;
    }

    public RoadmapTag(final Long id, final RoadmapTagName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public RoadmapTagName getName() {
        return name;
    }
}
