package co.kirikiri.domain.roadmap;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
