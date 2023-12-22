package co.kirikiri.roadmap.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapContents {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "roadmap")
    @Column(nullable = false)
    private final List<RoadmapContent> values = new ArrayList<>();

    public RoadmapContents(final List<RoadmapContent> contents) {
        this.values.addAll(contents);
    }

    public void add(final RoadmapContent content) {
        this.values.add(content);
    }

    public Optional<RoadmapContent> findLastRoadmapContent() {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(values.get(values.size() - 1));
    }

    public List<RoadmapContent> getValues() {
        return values;
    }
}
