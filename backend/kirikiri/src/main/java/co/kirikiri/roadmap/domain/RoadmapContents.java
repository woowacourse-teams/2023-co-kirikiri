package co.kirikiri.roadmap.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapContents {

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
