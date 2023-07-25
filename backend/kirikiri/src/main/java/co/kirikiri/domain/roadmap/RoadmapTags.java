package co.kirikiri.domain.roadmap;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapTags {

    private static final int MAX_COUNT = 5;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "roadmap_id", updatable = false, nullable = false)
    private final List<RoadmapTag> values = new ArrayList<>();

    public RoadmapTags(final List<RoadmapTag> roadmapTags) {
        validate(roadmapTags);
        values.addAll(new ArrayList<>(roadmapTags));
    }

    private void validate(final List<RoadmapTag> roadmapTags) {
        if (roadmapTags.size() > MAX_COUNT) {
            throw new BadRequestException(
                    String.format("태그의 개수는 최대 %d개까지 가능합니다.", MAX_COUNT));
        }
    }
}
