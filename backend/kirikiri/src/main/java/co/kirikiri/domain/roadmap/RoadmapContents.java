package co.kirikiri.domain.roadmap;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapContents {

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roadmap")
    @Column(nullable = false)
    private final List<RoadmapContent> values = new ArrayList<>();

    public RoadmapContents(final List<RoadmapContent> contents) {
        this.values.addAll(new ArrayList<>(contents));
    }

    public void add(final RoadmapContent content) {
        this.values.add(content);
    }

    public List<RoadmapContent> getValues() {
        return new ArrayList<>(values);
    }
}
