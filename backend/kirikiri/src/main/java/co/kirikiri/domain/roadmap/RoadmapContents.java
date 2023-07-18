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
    private final List<RoadmapContent> contents = new ArrayList<>();

    public RoadmapContents(final List<RoadmapContent> contents) {
        this.contents.addAll(contents);
    }

    public void add(final RoadmapContent content) {
        this.contents.add(content);
    }

    public List<RoadmapContent> getContents() {
        return contents;
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean hasContent(final RoadmapContent content) {
        return contents.contains(content);
    }

    public void removeContent(final RoadmapContent content) {
        contents.remove(content);
    }
}
