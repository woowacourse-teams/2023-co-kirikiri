package co.kirikiri.domain.roadmap;

import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoadmapContents {

    private static final int ROADMAP_CONTENTS_MIN_SIZE = 1;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roadmap")
    @Column(nullable = false)
    private final List<RoadmapContent> contents = new ArrayList<>();

    public RoadmapContents(final List<RoadmapContent> contents) {
        validate(contents);
        this.contents.addAll(contents);
    }

    private void validate(final List<RoadmapContent> contents) {
        if (contents.size() < ROADMAP_CONTENTS_MIN_SIZE) {
            throw new BadRequestException("로드맵의 정보는 " + ROADMAP_CONTENTS_MIN_SIZE + "개 이상 존재해야 합니다.");
        }
    }

    public void add(final RoadmapContent content) {
        this.contents.add(content);
    }
}
