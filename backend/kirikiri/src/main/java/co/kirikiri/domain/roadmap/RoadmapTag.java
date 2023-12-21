package co.kirikiri.domain.roadmap;

import co.kirikiri.common.entity.BaseEntity;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RoadmapTag that = (RoadmapTag) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName());
    }

    @Override
    public Long getId() {
        return id;
    }

    public RoadmapTagName getName() {
        return name;
    }
}
